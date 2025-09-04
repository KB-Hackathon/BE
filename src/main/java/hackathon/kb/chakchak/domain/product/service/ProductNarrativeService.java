package hackathon.kb.chakchak.domain.product.service;

import hackathon.kb.chakchak.domain.member.repository.SellerRepository;
import hackathon.kb.chakchak.domain.product.api.dto.ProductMetaRequest;
import hackathon.kb.chakchak.domain.product.domain.entity.InstaPrompt;
import hackathon.kb.chakchak.domain.product.repository.InstaPromptRepository;
import hackathon.kb.chakchak.domain.product.service.dto.NarrativeResult;
import hackathon.kb.chakchak.global.s3.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductNarrativeService {
    private final SellerRepository sellerRepository;
    private final InstaPromptRepository instaPromptRepository;
    private final OpenAIMultimodalNarrativeService openAIMultimodalNarrativeService;
    private final S3StorageService s3StorageService;

    @Transactional(readOnly = false)
    public NarrativeResult createNarrative(Long memberId, ProductMetaRequest meta, List<MultipartFile> images) {
        // 1) 이미지 S3 업로드
        List<String> imageUrls = s3StorageService
                .uploadImages(images, "products")
                .stream()
                .map(URL::toString)
                .toList();

        // 2) 카테고리 프롬프트 조회
        InstaPrompt prompt = instaPromptRepository
                .findTopByCategory(meta.getCategory())
                .orElseThrow(() -> new IllegalStateException("해당 카테고리 프롬프트가 없습니다."));

        // 3) 가게명 조회
//        String companyName = sellerRepository.findById(memberId)
//                .map(Seller::getCompanyName)
//                .orElseThrow(() -> new IllegalStateException("Seller 정보가 없습니다."));

        // 4) 프롬프트 구성
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
                """.formatted(prompt.getContent());

        String userText = """
                    [상품 정보]
                    - 가게명: %s
                    - 상품명: %s
                    - 카테고리: %s
                    - 설명: %s
                """.formatted(
                "스타벅스 합정점", //companyName
                meta.getTitle(),
                meta.getCategory(),
                meta.getDescription()
        );

        // 5) GPT 호출
        return openAIMultimodalNarrativeService.generateNarrativeWithImageUrls(
                systemInstruction, userText, imageUrls
        );
    }
}
