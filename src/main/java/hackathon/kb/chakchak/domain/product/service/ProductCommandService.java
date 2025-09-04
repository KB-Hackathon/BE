package hackathon.kb.chakchak.domain.product.service;

import hackathon.kb.chakchak.domain.capture.domain.Capture;
import hackathon.kb.chakchak.domain.capture.repository.CaptureRepository;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveRequest;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.domain.entity.Tag;
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
    private final CaptureRepository captureRepository;

    public Long saveProduct(ProductSaveRequest req) {
        if (req == null || req.getProductId() == null) {
            throw new IllegalArgumentException("productId가 필요합니다.");
        }

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new IllegalStateException("Product 없음: id=" + req.getProductId()));

        // 1) endCaptureId: 매번 새로운 capture 생성 → PK 세팅
        Long newEndCaptureId = captureRepository.save(new Capture()).getId();
        product.changeEndCaptureId(newEndCaptureId);

        // 2) 단순 필드 업데이트 (null 이면 기존값 유지)
        product.changeDescription(req.getDescription());
        product.changePrice(req.getPrice());
        product.changeCoupon(req.getIsCoupon());
        product.changeTargetAmount(req.getTargetAmount());
        product.changeRecruitmentPeriods(req.getRecruitmentStartPeriod(), req.getRecruitmentEndPeriod());

        // 3) status는 무조건 PENDING
        product.markPending();

        // 4) refreshedAt 갱신
        product.touchRefreshedAt(LocalDateTime.now());

        // 5) 태그 갈아끼우기
        if (req.getTags() != null) {
            List<Tag> toSave = new ArrayList<>();
            for (String name : req.getTags()) {
                if (name == null || name.isBlank()) continue;
                toSave.add(Tag.builder()
                        .product(product)
                        .name(name.trim())
                        .build());
            }
            if (!toSave.isEmpty()) tagRepository.saveAll(toSave);
        }

        // 영속성 컨텍스트 변경감지로 UPDATE 수행됨
        return product.getId();
    }
}
