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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageOverlayService {
    private final S3StorageService s3StorageService;

    public String processFirstImage(String src, String companyName, String title, String line2) {

        // 텍스트
        String line1 = companyName + " | " + title;

        // 한글 폰트 주의: 서버/OS에 따라 폰트명이 다릅니다. (아래 순서대로 시도)
        Font titleFont = pickFont(new String[]{
                "Apple SD Gothic Neo", "Malgun Gothic", "Noto Sans CJK KR", "NanumGothic"
        }, Font.BOLD,  Math.round(43));  // 제목 크기
        Font subFont   = pickFont(new String[]{
                "Apple SD Gothic Neo", "Malgun Gothic", "Noto Sans CJK KR", "NanumGothic"
        }, Font.PLAIN, Math.round(34));  // 부제 크기

        try {
            return overlayBottomLeft(src, line1, line2, titleFont, subFont).toString();
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.S3_UPLOAD_FAIL);
        }
    }

    private BufferedImage cropToTwoThree(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        double targetRatio = 2.0 / 3.0;

        int newW = w;
        int newH = h;

        double currentRatio = (double) w / h;

        if (currentRatio > targetRatio) {
            // 원본이 더 가로로 넓음 → 가로를 줄여야 함
            newW = (int) Math.round(h * targetRatio);
        } else {
            // 원본이 더 세로로 길거나 같음 → 세로를 줄여야 함
            newH = (int) Math.round(w / targetRatio);
        }

        int x = (w - newW) / 2;
        int y = (h - newH) / 2;

        return src.getSubimage(x, y, newW, newH);
    }

    private URL overlayBottomLeft(String src,
                                  String title, String subtitle,
                                  Font titleFont, Font subtitleFont) throws Exception {

        // 1. 원본 불러오기
        BufferedImage img = ImageIO.read(new URL(src));

        // 2. 정사각형으로 크롭
        img = cropToTwoThree(img);

        int w = img.getWidth();
        int h = img.getHeight();

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 하단 그라데이션
        int padY = Math.max(40, h / 20);
        int barH = padY + (int)(getTextHeight(g, titleFont) + getTextHeight(g, subtitleFont)) + padY;
        paintBottomGradient(g, w, h, barH);

        // 좌측 패딩 (글자와 화면 왼쪽 간격)
        int padX = Math.max(40, w / 30);

        // 텍스트 Y 배치 (하단 기준)
        int y = h - padY - (int)getTextHeight(g, subtitleFont);

        // 부제
        g.setFont(subtitleFont);
        drawLeftAlignedOutlinedText(g, subtitle, padX, y, Color.WHITE, new Color(0,0,0,150), 2f);

        // 제목 (그 위에)
        y -= (int)getTextHeight(g, titleFont) + Math.max(10, h/200);
        g.setFont(titleFont);
        drawLeftAlignedOutlinedText(g, title, padX, y, Color.WHITE, new Color(0,0,0,180), 3f);

        g.dispose();

        // 3. BufferedImage → ByteArrayOutputStream 변환
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", os);

        // 4. ByteArrayOutputStream → MultipartFile 변환
        byte[] bytes = os.toByteArray();
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "overlayed.jpg",
                "image/jpeg",
                new ByteArrayInputStream(bytes)
        );

        // 5. S3 업로드 호출
        return s3StorageService.uploadImages(multipartFile);  // 기존 S3 업로드 서비스 호출
    }

    /** 왼쪽 정렬 + 외곽선 텍스트 */
    private void drawLeftAlignedOutlinedText(Graphics2D g, String text, int leftX, int baselineY,
                                            Color fillColor, Color strokeColor, float strokeWidth) {
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector gv = g.getFont().createGlyphVector(frc, text);
        Shape shape = gv.getOutline(leftX, baselineY);


        // 내부 채움
        g.setColor(fillColor);
        g.fill(shape);
    }

    /** 하단에 투명 → 블랙 반투명 그라데이션 */
    private void paintBottomGradient(Graphics2D g, int width, int height, int barH) {
        GradientPaint gp = new GradientPaint(
                0, height - barH, new Color(0,0,0,0),
                0, height, new Color(0,0,0,170)
        );
        Paint old = g.getPaint();
        g.setPaint(gp);
        g.fillRect(0, height - barH, width, barH);
        g.setPaint(old);
    }

    /** 상단에 위쪽이 짙은 반투명 블랙 → 투명 그라데이션 */
    private void paintTopGradient(Graphics2D g, int width, int height) {
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(0,0,0,170),
                0, height, new Color(0,0,0,0)
        );
        Paint old = g.getPaint();
        g.setPaint(gp);
        g.fillRect(0, 0, width, height);
        g.setPaint(old);
    }

    /** 가운데 정렬 + 외곽선 텍스트 */
    private void drawCenteredOutlinedText(Graphics2D g, String text, int centerX, int baselineY,
                                         Color fillColor, Color strokeColor, float strokeWidth) {
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector gv = g.getFont().createGlyphVector(frc, text);
        Shape shape = gv.getOutline();
        Rectangle2D bounds = shape.getBounds2D();

        // 가운데 정렬을 위해 x 오프셋
        double x = centerX - bounds.getCenterX();
        double y = baselineY - bounds.getY(); // baseline 보정

        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);

        // 스트로크(외곽선)
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(strokeColor);
        g2.draw(shape);

        // 내부 채움
        g2.setColor(fillColor);
        g2.fill(shape);

        g2.dispose();
    }

    private float getTextHeight(Graphics2D g, Font font) {
        FontMetrics fm = g.getFontMetrics(font);
        return fm.getAscent() + fm.getDescent();
    }

    /** 가능한 폰트명을 순회하며 첫 가용 폰트를 고름 (없으면 Dialog 대체) */
    private Font pickFont(String[] candidates, int style, int size) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        var installed = java.util.Set.of(ge.getAvailableFontFamilyNames());
        for (String name : candidates) {
            if (installed.contains(name)) return new Font(name, style, size);
        }
        return new Font("Dialog", style, size);
    }
}
