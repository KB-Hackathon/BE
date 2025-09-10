package hackathon.kb.chakchak.domain.product.service;

import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.member.repository.SellerRepository;
import hackathon.kb.chakchak.domain.product.api.dto.ProductMetaRequest;
import hackathon.kb.chakchak.domain.product.api.dto.ProductMetaResponse;
import hackathon.kb.chakchak.domain.product.domain.entity.InstaPrompt;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus;
import hackathon.kb.chakchak.domain.product.repository.InstaPromptRepository;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import hackathon.kb.chakchak.domain.product.service.dto.NarrativeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductNarrativeService {
    private final SellerRepository sellerRepository;
    private final InstaPromptRepository instaPromptRepository;
    private final ProductRepository productRepository;

    private final OpenAIMultimodalNarrativeService openAIMultimodalNarrativeService;

    @Transactional(readOnly = false)
    public ProductMetaResponse createNarrative(Long memberId, ProductMetaRequest meta) {
        // 0) seller
        Seller seller = sellerRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Seller 정보가 없습니다."));

        // 1) Product 즉시 저장 (nullable=false 필드에 기본값 채우기)
        LocalDateTime now = LocalDateTime.now();
        Product product = Product.builder()
                .seller(seller)
                .endCaptureId(0L) // 기본값(업데이트 예정)
                .title(meta.getTitle())
                .category(meta.getCategory())
                .price(BigDecimal.valueOf(0L)) // 기본값(업데이트 예정)
                .description(meta.getSummary()) // 기본값(업데이트 예정)
                .tmpSummary("") //기본값(업데이트 예정)
                .status(ProductStatus.DRAFT) // 임시 상태
                .targetAmount(null) // 기본값(업데이트 예정)
                .recruitmentStartPeriod(now) // 기본값(업데이트 예정)
                .recruitmentEndPeriod(now.plusDays(30)) // 기본값(업데이트 예정)
                .refreshCnt((short) 0) // 기본값(업데이트 예정)
                .refreshedAt(now) // 기본값(업데이트 예정)
                .build();

        product = productRepository.save(product);
        String purchaseUrl = "https://chakchak-nu.vercel.app/product/" + product.getId();

        // 2) 이미지 URL
        List<String> imageUrls = meta.getImages();

        // 3) 카테고리 프롬프트 조회
        InstaPrompt prompt = instaPromptRepository
                .findTopByCategory(meta.getCategory())
                .orElseThrow(() -> new IllegalStateException("해당 카테고리 프롬프트가 없습니다."));

        // 4) 피처 기반 가이드 구성
        String featureGuide = """
                [스타일 피처 요약]
                - 키워드 Top-N: %s
                - 이모지 비율: %.2f
                - 해시태그 수: %d
                - 문장 길이 분포: %s
                - CTA 유형: %s
                """.formatted(
                prompt.getTopKeywords(),
                prompt.getEmojiRatio(),
                prompt.getHashtagCount(),
                prompt.getSentenceLenHistJson(),
                prompt.getCtaTypesJson()
        );

        // 5) 프롬프트 구성
        String systemInstruction = """
                당신은 감성적인 인스타그램 카피라이터야.
                아래 '카테고리 전용 가이드'와 '상품 정보'를 최대한 반영해,
                아래 세 가지를 반드시 작성해줘.
                
                1) 스토리텔링 멘트
                - 상품을 소개할 때 단순 나열 말고, 분위기·맛·경험을 담아내라.\s
                - 적절한 이모지나 특수기호(𓏸, 𓂃, ⁺⸜ 등)을 섞어 분위기를 살려라.
                2) 상품 한줄 소개 (상품의 핵심 조합/풍미를 간단히 요약)
                3) 해시태그는 (6~10개, 감성 + 장소 + 카테고리 혼합으로)
                
                출력 형식은 반드시 아래의 JSON만 반환해.
                백틱, 주석, 불필요한 텍스트, 설명 금지. 마크다운 금지.
                
                {
                  "caption": "<문장형 줄글 캡션>",
                  "summary": "<상품 한줄 소개>",
                  "hashtags": "<해시태그 문자열(#태그 공백 구분)>"
                }
                
                [카테고리 전용 가이드]
                %s
                """.formatted(featureGuide);

        String userText = """
                    [상품 정보]
                    - 가게명: %s
                    - 상품명: %s
                    - 카테고리: %s
                    - 설명: %s
                """.formatted(
                seller.getCompanyName(),
                meta.getTitle(),
                meta.getCategory(),
                meta.getSummary()
//                meta.getPrice(),
//                purchaseUrl,
//                seller.getRoadNameAddress()
        );

        // 6) GPT 호출
        NarrativeResult nr = openAIMultimodalNarrativeService.generateNarrativeWithImageUrls(
                systemInstruction, userText, imageUrls
        );

        // 6-1) GPT summary를 Product.tmpSummary 에 저장
        if (nr != null && nr.getSummary() != null) {
            product.changeTmpSummary(nr.getSummary().trim());
            productRepository.save(product); // 트랜잭션 안이라 merge 동작
        }

        // 7) 게시글(caption) 작성
        return generateProductMetaResponse(
                product.getId(),
                nr,
                meta.getTitle(),
                meta.getPrice(),
                meta.getRecruitmentEndPeriod(),
                purchaseUrl,
                seller.getCompanyName(),
                seller.getRoadNameAddress()
                );
    }

    private ProductMetaResponse generateProductMetaResponse(
            Long productId,
            NarrativeResult nr,
            String title,
            BigDecimal price,
            LocalDateTime recruitmentEndPeriod,
            String purchaseUrl,
            String companyName,
            String roadNameAddress
    ) {
        String caption = nr != null && nr.getCaption() != null ? nr.getCaption().trim() : "";
        String summary = nr != null && nr.getSummary() != null ? nr.getSummary().trim() : "";
        List<String> hashtags = nr != null && nr.getHashtags() != null ? nr.getHashtags() : Collections.emptyList();

        StringBuilder sb = new StringBuilder();

        if (!caption.isEmpty()) sb.append(caption);

        // 상품명 | 가격
        if ((!isBlank(title)) || price != null || (!isBlank(purchaseUrl))) {
            if (sb.length() > 0) sb.append("\n\n");
            if (!isBlank(title)) sb.append(title.trim());
            if (price != null) {
                if (!isBlank(title)) sb.append(" | ");
                sb.append(formatKrw(price)).append("원");
            }
            sb.append("\n");

            // 마감일 + 구매링크 라인
            if (recruitmentEndPeriod != null) {
                sb.append("✨ ").append(formatKoreanDate(recruitmentEndPeriod)).append("까지 한정 할인!");
                if (!isBlank(purchaseUrl)) {
                    sb.append(" 👉 [구매하기](").append(purchaseUrl.trim()).append(")");
                }
            } else if (!isBlank(purchaseUrl)) {
                // 마감일이 없으면 링크만
                sb.append("👉 [구매하기](").append(purchaseUrl.trim()).append(")");
            }
        }

        // : summary
        if (!isBlank(summary)) {
            sb.append("\n");
            if (!summary.startsWith(":")) sb.append(": ");
            sb.append(summary);
        }

        // [가게명] 주소
        if (!isBlank(companyName) || !isBlank(roadNameAddress)) {
            sb.append("\n\n");
            if (!isBlank(companyName)) {
                sb.append("\uD83D\uDCCD[").append(companyName.trim()).append("]");
                if (!isBlank(roadNameAddress)) sb.append(" ");
            }
            if (!isBlank(roadNameAddress)) sb.append(roadNameAddress.trim());
        }

        return new ProductMetaResponse(productId, sb.toString().trim(), hashtags);
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String formatKrw(BigDecimal n) { return new DecimalFormat("#,##0").format(n); }
    private static String formatKoreanDate(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN));
    }
}
