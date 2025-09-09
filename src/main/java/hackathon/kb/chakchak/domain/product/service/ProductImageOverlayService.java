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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

    // 클램프(과도한 확대/축소 방지)
    private static final int MIN_TITLE = 12,  MAX_TITLE = 140;
    private static final int MIN_SUB   = 10,  MAX_SUB   = 120;

    // 폰트 후보
    private static final String[] FONT_CANDIDATES = {
            "Apple SD Gothic Neo", "Malgun Gothic", "Noto Sans CJK KR", "NanumGothic"
    };

    public String processFirstImage(String src, String companyName, String title, String line2) {
        String line1 = (companyName == null ? "" : companyName) + " | " + (title == null ? "" : title);

        try {
            URL uploaded = overlayBottomLeftScaled(src, line1.trim(), safe(line2));
            return uploaded.toString();
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.S3_UPLOAD_FAIL);
        }
    }

    /** 이미지 로딩 → 2:3 크롭 → 폰트 스케일 → 폭 초과 시 2차 축소 → 하단 그라데이션 + 좌측 정렬 텍스트 → S3 업로드 */
    private URL overlayBottomLeftScaled(String src,
                                        String title, String subtitle) throws Exception {

        // 1) 이미지 로드 (타임아웃)
        BufferedImage img = readImageWithTimeout(src, 5000, 5000);

        // 2) 2:3 비율로 크롭
        img = cropToTwoThree(img);

        int w = img.getWidth();
        int h = img.getHeight();

        // 3) 해상도 기반 1차 스케일
        double scale = Math.min(w / (double) BASE_W, h / (double) BASE_H);
        int titleSize = clamp((int) Math.round(BASE_TITLE * scale), MIN_TITLE, MAX_TITLE);
        int subSize   = clamp((int) Math.round(BASE_SUB   * scale), MIN_SUB,   MAX_SUB);

        Font titleFont    = pickFont(FONT_CANDIDATES, Font.BOLD,  titleSize);
        Font subtitleFont = pickFont(FONT_CANDIDATES, Font.PLAIN, subSize);

        // ===== 마진 설정 (여기서 조절) =====
        int padLeft   = Math.max(40,  (int)Math.round(w * 0.05)); // 왼쪽 마진
        int padRight  = Math.max(40,  (int)Math.round(w * 0.05)); // 오른쪽 마진
        int padBottom = Math.max(40,  (int)Math.round(h * 0.05)); // 아래쪽 마진
        int spacing   = Math.max(10,  h / 200);                   // 제목–부제 간격
        // ==================================

        // 텍스트 최대 폭: 좌/우 마진을 제외한 영역
        int maxWidthPx = w - padLeft - padRight;

        // 미리 렌더링 컨텍스트로 실제 폭 측정
        Graphics2D gMeasure = img.createGraphics();
        gMeasure.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        FontRenderContext frc = gMeasure.getFontRenderContext();

        double titleWidth = stringWidth(titleFont, title, frc);
        double subWidth   = (subtitle == null || subtitle.isBlank()) ? 0 : stringWidth(subtitleFont, subtitle, frc);
        gMeasure.dispose();

        double overRatio = 1.0;
        if (titleWidth > maxWidthPx) overRatio = Math.max(overRatio, titleWidth / maxWidthPx);
        if (subWidth   > maxWidthPx) overRatio = Math.max(overRatio, subWidth   / maxWidthPx);

        if (overRatio > 1.0) {
            double k = 0.98 / overRatio; // 살짝 여유
            titleSize = clamp((int)Math.floor(titleSize * k), MIN_TITLE, MAX_TITLE);
            subSize   = clamp((int)Math.floor(subSize   * k), MIN_SUB,   MAX_SUB);
            titleFont    = titleFont.deriveFont((float) titleSize);
            subtitleFont = subtitleFont.deriveFont((float) subSize);
        }

        // 5) 실제 그리기
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            float titleH = textHeight(g, titleFont);
            float subH   = (subtitle == null || subtitle.isBlank()) ? 0 : textHeight(g, subtitleFont);

            // 그라데이션 바 높이 = 아래 패딩 + 텍스트(제목 + 부제 + 간격) + 아래 패딩
            int textBlockH = Math.round(titleH + (subH > 0 ? (spacing + subH) : 0));
            int barH = padBottom + textBlockH + padBottom;
            paintBottomGradient(g, w, h, barH);

            // 베이스라인: 하단 마진 기준
            int baselineY = h - padBottom;

            // 부제 (아래쪽)
            if (subH > 0) {
                g.setFont(subtitleFont);
                float subStroke = Math.max(1.5f, subSize * 0.06f);
                drawLeftAlignedOutlinedText(g, subtitle, padLeft, baselineY,
                        Color.WHITE, new Color(0,0,0,170), subStroke);
                baselineY -= Math.round(subH + spacing);
            }

            // 제목 (그 위)
            g.setFont(titleFont);
            float titleStroke = Math.max(2.0f, titleSize * 0.07f);
            drawLeftAlignedOutlinedText(g, title, padLeft, baselineY,
                    Color.WHITE, new Color(0,0,0,200), titleStroke);

        } finally {
            g.dispose();
        }

        // 6) JPEG 인코딩 → S3 업로드
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", os);
        byte[] bytes = os.toByteArray();

        MultipartFile mf = new MockMultipartFile(
                "file", "overlayed.jpg", "image/jpeg", new ByteArrayInputStream(bytes)
        );
        return s3StorageService.uploadImages(mf);
    }

    /** 왼쪽 정렬 + 외곽선 텍스트 */
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
            g2.draw(shape);          // 외곽선
            g2.setColor(fillColor);
            g2.fill(shape);          // 내부 채움
        } finally {
            g2.dispose();
        }
    }

    /** 하단에 투명 → 반투명 블랙 그라데이션 바 */
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

    /** 2:3 비율로 중앙 크롭 */
    private BufferedImage cropToTwoThree(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        double target = 2.0 / 3.0;     // width / height

        int newW = w, newH = h;
        double cur = w / (double) h;

        if (cur > target) newW = (int) Math.round(h * target);        // 가로가 더 넓음 → 가로 잘라냄
        else              newH = (int) Math.round(w / target);        // 세로가 더 김 → 세로 잘라냄

        int x = (w - newW) / 2;
        int y = (h - newH) / 2;
        return src.getSubimage(x, y, newW, newH);
    }

    /** 텍스트 높이(= ascent + descent) */
    private float textHeight(Graphics2D g, Font f) {
        FontMetrics fm = g.getFontMetrics(f);
        return fm.getAscent() + fm.getDescent();
    }

    /** 문자열 폭 */
    private double stringWidth(Font f, String s, FontRenderContext frc) {
        if (s == null || s.isBlank()) return 0;
        return f.getStringBounds(s, frc).getWidth();
    }

    /** 가능한 폰트명을 순회하며 첫 가용 폰트를 고름 (없으면 Dialog 대체) */
    private Font pickFont(String[] candidates, int style, int size) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Set<String> installed = new HashSet<>(Arrays.asList(ge.getAvailableFontFamilyNames()));
        for (String name : candidates) {
            if (installed.contains(name)) return new Font(name, style, size);
        }
        return new Font("Dialog", style, size);
    }

    /** 네트워크 이미지 로드에 타임아웃 부여 */
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
}
