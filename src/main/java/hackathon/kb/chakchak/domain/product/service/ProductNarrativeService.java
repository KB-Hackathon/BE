package hackathon.kb.chakchak.domain.product.service;

import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.member.repository.SellerRepository;
import hackathon.kb.chakchak.domain.product.api.dto.ProductMetaRequest;
import hackathon.kb.chakchak.domain.product.domain.entity.Image;
import hackathon.kb.chakchak.domain.product.domain.entity.InstaPrompt;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus;
import hackathon.kb.chakchak.domain.product.repository.ImageRepository;
import hackathon.kb.chakchak.domain.product.repository.InstaPromptRepository;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import hackathon.kb.chakchak.domain.product.service.dto.NarrativeResult;
import hackathon.kb.chakchak.global.s3.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductNarrativeService {
    private final SellerRepository sellerRepository;
    private final InstaPromptRepository instaPromptRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    private final OpenAIMultimodalNarrativeService openAIMultimodalNarrativeService;
    private final S3StorageService s3StorageService;

    @Transactional(readOnly = false)
    public NarrativeResult createNarrative(Long memberId, ProductMetaRequest meta, List<MultipartFile> images) {
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
                .price(0L) // 기본값(업데이트 예정)
                .description(meta.getDescription())
                .status(ProductStatus.DRAFT) // 임시 상태
                .targetAmount(null) // 기본값(업데이트 예정)
                .recruitmentStartPeriod(now) // 기본값(업데이트 예정)
                .recruitmentEndPeriod(now.plusDays(30)) // 기본값(업데이트 예정)
                .refreshCnt((short) 0) // 기본값(업데이트 예정)
                .refreshedAt(now) // 기본값(업데이트 예정)
                .build();

        product = productRepository.save(product);

        // 2) 이미지 URL들 ProductImage로 저장
        List<String> imageUrls = s3StorageService
                .uploadImages(images, "products")
                .stream()
                .map(URL::toString)
                .toList();

        List<Image> imgs = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            imgs.add(Image.builder()
                    .product(product)
                    .url(imageUrls.get(i))
                    .build());
        }
        imageRepository.saveAll(imgs);

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
                아래 '카테고리 전용 가이드'와 '상품 정보'를 반영해,
                실제 브랜드가 올리는 인스타 캡션처럼 **스토리텔링 있는 줄글**을 작성해줘.
                
                        - 매장/상품을 소개할 때 단순 나열 말고, 분위기·맛·경험을 담아내라. 
                        - 적절한 이모지나 특수기호(𓏸, 𓂃, ⁺⸜ 등)을 섞어 분위기를 살려라.
                        - 홍보 문구 같지 않고 자연스럽게 초대하듯 마무리해라.
                        - 해시태그는 8~12개, 감성 + 장소 + 카테고리 혼합으로.
                
                출력 형식은 반드시 아래의 JSON만 반환해.
                백틱, 주석, 불필요한 텍스트, 설명 금지. 마크다운 금지.
                
                {
                  "caption": "<문장형 줄글 캡션>",
                  "hashtags": "<해시태그 문자열(#태그 공백 구분, 6~10개)>"
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
                "스타벅스 합정점", // seller.getCompanyName()
                meta.getTitle(),
                meta.getCategory(),
                meta.getDescription()
        );

        // 6) GPT 호출
        return openAIMultimodalNarrativeService.generateNarrativeWithImageUrls(
                product.getId(), systemInstruction, userText, imageUrls
        );
    }
}
