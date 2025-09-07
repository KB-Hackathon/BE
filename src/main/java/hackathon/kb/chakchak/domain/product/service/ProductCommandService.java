package hackathon.kb.chakchak.domain.product.service;

import hackathon.kb.chakchak.domain.capture.domain.Capture;
import hackathon.kb.chakchak.domain.capture.repository.CaptureRepository;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.member.repository.SellerRepository;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveRequest;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveResponse;
import hackathon.kb.chakchak.domain.product.domain.entity.Image;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.domain.entity.Tag;
import hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus;
import hackathon.kb.chakchak.domain.product.repository.ImageRepository;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import hackathon.kb.chakchak.domain.product.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;
    private final CaptureRepository captureRepository;
    private final SellerRepository sellerRepository;

    /**
     * 새 상품 저장 (생성)
     * @param sellerId 판매자 ID
     * @param req      요청 DTO
     * @return 생성된 상품 ID
     */
    public ProductSaveResponse saveProduct(Long sellerId, ProductSaveRequest req) {
        // 생성 전 유효성 간단 체크
        if (req == null) throw new IllegalArgumentException("요청이 비어 있습니다.");
        if (req.getTitle() == null || req.getTitle().isBlank()) throw new IllegalArgumentException("title은 필수입니다.");
        if (req.getPrice() == null) throw new IllegalArgumentException("price는 필수입니다.");
        if (req.getDescription() == null || req.getDescription().isBlank()) throw new IllegalArgumentException("description은 필수입니다.");
        if (req.getRecruitmentStartPeriod() == null || req.getRecruitmentEndPeriod() == null)
            throw new IllegalArgumentException("모집 기간(recruitmentStart/End)은 필수입니다.");

        // 판매자 로드
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다. id=" + sellerId));

        // 캡처 생성 → endCaptureId 세팅
        Long endCaptureId = captureRepository.save(new Capture()).getId();

        // 쿠폰 메타 변환
        boolean useCoupon = Boolean.TRUE.equals(req.getIsCoupon());

        // 상품 엔티티 생성
        Product product = Product.builder()
                .seller(seller)
                .endCaptureId(endCaptureId)
                .title(req.getTitle())
                .category(req.getCategory())
                .price(req.getPrice())
                .description(req.getDescription())
                .status(ProductStatus.PENDING)
                .targetAmount(req.getTargetAmount())
                .isCoupon(useCoupon)
                .couponName(useCoupon ? req.getCouponName() : null)
                .couponExpiration(useCoupon ? req.getCouponExpiration() : null)
                .recruitmentStartPeriod(req.getRecruitmentStartPeriod())
                .recruitmentEndPeriod(req.getRecruitmentEndPeriod())
                .refreshCnt((short) 0)
                .refreshedAt(LocalDateTime.now())
                .build();

        // 먼저 상품 저장 (FK 필요)
        productRepository.save(product);

        // 태그 저장
        if (req.getTags() != null) {
            List<Tag> tags = new ArrayList<>();
            for (String name : req.getTags()) {
                if (name == null || name.isBlank()) continue;
                tags.add(Tag.builder()
                        .product(product)
                        .name(name.trim())
                        .build());
            }
            if (!tags.isEmpty()) tagRepository.saveAll(tags);
        }

        // 이미지 저장
        if (req.getImages() != null) {
            List<Image> images = new ArrayList<>();
            for (String url : req.getImages()) {
                if (url == null) continue;
                url = url.trim();
                if (url.isEmpty()) continue;

                images.add(Image.builder()
                        .product(product)
                        .url(url)
                        .build());
            }
            if (!images.isEmpty()) imageRepository.saveAll(images);
        }

        return new ProductSaveResponse(product.getId());
    }
}