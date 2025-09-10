package hackathon.kb.chakchak.domain.product.service;

import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import hackathon.kb.chakchak.global.s3.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageOverlayService {
    private final S3StorageService s3StorageService;

    // 기준 해상도/폰트 (810x1215에서 title=43, sub=34)
    private static final int BASE_W = 810;
    private static final int BASE_H = 1215;
    private static final int BASE_TITLE = 43;
    private static final int BASE_SUB   = 34;

    // 클램프
    private static final int MIN_TITLE = 12,  MAX_TITLE = 140;
    private static final int MIN_SUB   = 10,  MAX_SUB   = 120;

    // 폰트 후보
    private static final String[] FONT_CANDIDATES = {
            "Noto Sans CJK KR", "NanumGothic"
    };

    // 확장자 후보
    private static final Set<String> SUPPORTED_OUT = Set.of("jpg","jpeg","png");


    public String processFirstImage(String src, String companyName, String title, String line2) {
        String line1 = (companyName == null ? "" : companyName) + " | " + (title == null ? "" : title);

        try {
            URL uploaded = overlayBottomLeftScaled(src, line1.trim(), safe(line2));
            return uploaded.toString();
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.S3_UPLOAD_FAIL);
        }
    }

    /** 이미지 로딩 → 크롭 → 폰트 스케일 → 줄바꿈 처리 → 그라데이션/텍스트 → S3 업로드 */
    private URL overlayBottomLeftScaled(String src, String title, String subtitle) throws Exception {
        BufferedImage img = readImageWithTimeout(src, 5000, 5000);
        img = cropToTwoThree(img);

        int w = img.getWidth();
        int h = img.getHeight();

        double scale = Math.min(w / (double) BASE_W, h / (double) BASE_H);
        int titleSize = clamp((int) Math.round(BASE_TITLE * scale), MIN_TITLE, MAX_TITLE);
        int subSize   = clamp((int) Math.round(BASE_SUB   * scale), MIN_SUB,   MAX_SUB);

        Font titleFont    = pickFont(FONT_CANDIDATES, Font.BOLD,  titleSize);
        Font subtitleFont = pickFont(FONT_CANDIDATES, Font.PLAIN, subSize);

        int padLeft   = Math.max(40,  (int)Math.round(w * 0.05));
        int padRight  = Math.max(40,  (int)Math.round(w * 0.05));
        int padBottom = Math.max(40,  (int)Math.round(h * 0.05));
        int spacing   = Math.max(10,  h / 200);
        int lineGap   = Math.max(6,   subSize / 5);
        int titleLineGap = Math.max(6, titleSize / 5);

        int maxWidthPx = w - padLeft - padRight;

        Graphics2D gMeasure = img.createGraphics();
        FontRenderContext frc = gMeasure.getFontRenderContext();
        List<String> titleLines = wrapByWidth(safe(title), titleFont, frc, maxWidthPx);
        List<String> subLines   = wrapByWidth(safe(subtitle), subtitleFont, frc, maxWidthPx);
        gMeasure.dispose();

        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            float titleH = textHeight(g, titleFont);
            float subH   = textHeight(g, subtitleFont);

            int titleBlockH = titleLines.isEmpty() ? 0
                    : Math.round(titleLines.size() * titleH + Math.max(0, titleLines.size()-1) * titleLineGap);
            int subBlockH = subLines.isEmpty() ? 0
                    : Math.round(subLines.size() * subH + Math.max(0, subLines.size()-1) * lineGap);

            int between = (!titleLines.isEmpty() && !subLines.isEmpty()) ? spacing : 0;
            int textBlockH = subBlockH + between + titleBlockH;
            int barH = padBottom + textBlockH + padBottom;
            paintBottomGradient(g, w, h, barH);

            int baselineY = h - padBottom;

            if (!subLines.isEmpty()) {
                g.setFont(subtitleFont);
                float subStroke = Math.max(1.5f, subSize * 0.06f);
                for (int i = subLines.size() - 1; i >= 0; i--) {
                    drawLeftAlignedOutlinedText(g, subLines.get(i), padLeft, baselineY,
                            Color.WHITE, new Color(0,0,0,170), subStroke);
                    baselineY -= Math.round(subH + (i > 0 ? lineGap : 0));
                }
                baselineY -= between;
            }

            if (!titleLines.isEmpty()) {
                g.setFont(titleFont);
                float titleStroke = Math.max(2.0f, titleSize * 0.07f);
                for (int i = titleLines.size() - 1; i >= 0; i--) {
                    drawLeftAlignedOutlinedText(g, titleLines.get(i), padLeft, baselineY,
                            Color.WHITE, new Color(0,0,0,200), titleStroke);
                    baselineY -= Math.round(titleH + (i > 0 ? titleLineGap : 0));
                }
            }
        } finally {
            g.dispose();
        }

        // 인코딩(format은 src에서 결정) → S3 업로드
        String ext = guessExtFromSrc(src);                 // "jpg" or "png"
        String format = ext.equals("jpg") ? "jpg" : "png"; // ImageIO 포맷명
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        // JPG는 알파가 없으므로 RGB로 변환
        BufferedImage out = img;
        if ("jpg".equals(ext)) {
            BufferedImage rgb = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D gg = rgb.createGraphics();
            gg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            gg.drawImage(img, 0, 0, Color.BLACK, null);
            gg.dispose();
            out = rgb;
        }

        ImageIO.write(out, format, os);
        byte[] bytes = os.toByteArray();

        String baseName = stripExt(extractFileNameFromSrc(src));
        String fileName = baseName + "-overlay." + ext;   // ← src 이름 기반
        String contentType = mimeOf(ext);

        MultipartFile mf = new MockMultipartFile(
                "file", fileName, contentType, new ByteArrayInputStream(bytes)
        );

        return s3StorageService.uploadImages(mf);

    }

    private void drawLeftAlignedOutlinedText(Graphics2D g, String text, int leftX, int baselineY,
                                             Color fillColor, Color strokeColor, float strokeWidth) {
        if (text == null || text.isBlank()) return;
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector gv = g.getFont().createGlyphVector(frc, text);
        Shape shape = gv.getOutline(leftX, baselineY);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(strokeColor);
            g2.setColor(fillColor);
            g2.fill(shape);
        } finally {
            g2.dispose();
        }
    }

    private void paintBottomGradient(Graphics2D g, int width, int height, int barH) {
        GradientPaint gp = new GradientPaint(
                0, height - barH, new Color(0,0,0,0),
                0, height,       new Color(0,0,0,170)
        );
        Paint old = g.getPaint();
        g.setPaint(gp);
        g.fillRect(0, height - barH, width, barH);
        g.setPaint(old);
    }

    private BufferedImage cropToTwoThree(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        double target = 3.0 / 4.0;
        int newW = w, newH = h;
        double cur = w / (double) h;

        if (cur > target) newW = (int) Math.round(h * target);
        else              newH = (int) Math.round(w / target);

        int x = (w - newW) / 2;
        int y = (h - newH) / 2;
        return src.getSubimage(x, y, newW, newH);
    }

    private float textHeight(Graphics2D g, Font f) {
        FontMetrics fm = g.getFontMetrics(f);
        return fm.getAscent() + fm.getDescent();
    }

    private Font pickFont(String[] candidates, int style, int size) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Set<String> installed = new HashSet<>(Arrays.asList(ge.getAvailableFontFamilyNames()));
        for (String name : candidates) {
            if (installed.contains(name)) return new Font(name, style, size);
        }
        return new Font("Dialog", style, size);
    }

    private BufferedImage readImageWithTimeout(String src, int connectMs, int readMs) throws Exception {
        if (src.startsWith("http://") || src.startsWith("https://")) {
            URLConnection conn = new URL(src).openConnection();
            conn.setConnectTimeout(connectMs);
            conn.setReadTimeout(readMs);
            try (var in = conn.getInputStream()) {
                return ImageIO.read(in);
            }
        } else {
            return ImageIO.read(new java.io.File(src));
        }
    }

    private static int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }
    private static String safe(String s) { return s == null ? "" : s; }

    /** 문자열을 maxWidth에 맞춰 줄바꿈(공백 우선, 없으면 글자 단위) */
    private List<String> wrapByWidth(String text, Font font, FontRenderContext frc, int maxWidthPx) {
        List<String> lines = new ArrayList<>();
        if (text == null) return lines;
        String s = text.strip();
        if (s.isEmpty()) return lines;

        if (font.getStringBounds(s, frc).getWidth() <= maxWidthPx) {
            lines.add(s);
            return lines;
        }

        String[] tokens = s.split("\\s+");
        StringBuilder cur = new StringBuilder();

        for (String word : tokens) {
            String candidate = (cur.length() == 0) ? word : cur + " " + word;
            if (font.getStringBounds(candidate, frc).getWidth() <= maxWidthPx) {
                cur.setLength(0);
                cur.append(candidate);
            } else {
                if (cur.length() > 0) {
                    lines.add(cur.toString());
                    cur.setLength(0);
                }
                if (font.getStringBounds(word, frc).getWidth() <= maxWidthPx) {
                    cur.append(word);
                } else {
                    StringBuilder piece = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        String cand = piece.toString() + word.charAt(i);
                        if (font.getStringBounds(cand, frc).getWidth() <= maxWidthPx) {
                            piece.append(word.charAt(i));
                        } else {
                            if (piece.length() > 0) lines.add(piece.toString());
                            piece.setLength(0);
                            piece.append(word.charAt(i));
                        }
                    }
                    cur.append(piece);
                }
            }
        }
        if (cur.length() > 0) lines.add(cur.toString());
        return lines;
    }

    private String guessExtFromSrc(String src) {
        try {
            String path = src;
            if (src.startsWith("http")) path = new java.net.URL(src).getPath();
            int dot = path.lastIndexOf('.');
            if (dot >= 0) {
                String ext = path.substring(dot + 1).toLowerCase(Locale.ROOT);
                int q = ext.indexOf('?'); if (q >= 0) ext = ext.substring(0, q);
                int s = ext.indexOf(';'); if (s >= 0) ext = ext.substring(0, s);
                if (SUPPORTED_OUT.contains(ext)) return ext.equals("jpeg") ? "jpg" : ext;
            }
        } catch (Exception ignore) {}
        return "jpg"; // 알 수 없으면 JPG로
    }

    private String mimeOf(String ext) {
        return "png".equals(ext) ? "image/png" : "image/jpeg";
    }

    private String extractFileNameFromSrc(String src) {
        String path = src;
        try {
            if (src.startsWith("http")) path = new URL(src).getPath();
        } catch (Exception ignore) {}

        int slash = path.lastIndexOf('/');
        String name = (slash >= 0) ? path.substring(slash + 1) : path;

        int q = name.indexOf('?');   if (q >= 0)   name = name.substring(0, q);
        int hash = name.indexOf('#');if (hash >= 0)name = name.substring(0, hash);

        try { name = java.net.URLDecoder.decode(name, StandardCharsets.UTF_8.name()); }
        catch (Exception ignore) {}

        return (name == null || name.isBlank()) ? "image" : name;
    }

    private String stripExt(String name) {
        int dot = name.lastIndexOf('.');
        return (dot > 0) ? name.substring(0, dot) : name;
    }

}
