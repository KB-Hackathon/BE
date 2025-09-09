package hackathon.kb.chakchak.domain.product.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hackathon.kb.chakchak.domain.capture.domain.Capture;
import hackathon.kb.chakchak.domain.capture.repository.CaptureRepository;
import hackathon.kb.chakchak.domain.escrow.domain.entity.Escrow;
import hackathon.kb.chakchak.domain.escrow.repository.EscrowRepository;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.member.repository.SellerRepository;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveRequest;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveResponse;
import hackathon.kb.chakchak.domain.product.domain.entity.Image;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.domain.entity.Tag;
import hackathon.kb.chakchak.domain.product.repository.ImageRepository;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import hackathon.kb.chakchak.domain.product.repository.TagRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandService {

	private final ProductRepository productRepository;
	private final TagRepository tagRepository;
	private final ImageRepository imageRepository;
	private final CaptureRepository captureRepository;
	private final SellerRepository sellerRepository;
	private final EscrowRepository escrowRepository;

	private final ProductImageOverlayService productImageOverlayService;

	/**
	 * 새 상품 저장 (생성)
	 * @param sellerId 판매자 ID
	 * @param req      요청 DTO
	 * @return 생성된 상품 ID
	 */
	public ProductSaveResponse saveProduct(Long sellerId, ProductSaveRequest req) {
		// 생성 전 유효성 간단 체크
		if (req == null)
			throw new IllegalArgumentException("요청이 비어 있습니다.");
		if (req.getTitle() == null || req.getTitle().isBlank())
			throw new IllegalArgumentException("title은 필수입니다.");
		if (req.getPrice() == null)
			throw new IllegalArgumentException("price는 필수입니다.");
		if (req.getDescription() == null || req.getDescription().isBlank())
			throw new IllegalArgumentException("description은 필수입니다.");
		if (req.getRecruitmentStartPeriod() == null || req.getRecruitmentEndPeriod() == null)
			throw new IllegalArgumentException("모집 기간(recruitmentStart/End)은 필수입니다.");

		// 판매자 로드
		Seller seller = sellerRepository.findById(sellerId)
			.orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다. id=" + sellerId));

		// 캡처 생성 → endCaptureId 세팅
		Long endCaptureId = captureRepository.save(new Capture()).getId();

		Product product = productRepository.findById(req.getProductId())
			.orElseThrow(() -> new IllegalArgumentException("상품이 없습니다."));

		// 엔티티 필드 변경 (dirty checking으로 update)
		product.changeDescription(req.getDescription());
		product.changePrice(req.getPrice());
		product.changeTargetAmount(req.getTargetAmount());
		product.changeCoupon(req.getIsCoupon());
		product.changeEndCaptureId(endCaptureId);
		product.changeRecruitmentPeriods(req.getRecruitmentStartPeriod(), req.getRecruitmentEndPeriod());
		product.touchRefreshedAt(LocalDateTime.now());
		product.markPending(); // 상태를 PENDING으로 변경

		product.changeTitle(req.getTitle());
		product.changeCategory(req.getCategory());
		product.changeCouponName(req.getCouponName());
		product.changeCouponExpiration(req.getCouponExpiration());

		Escrow escrow = Escrow.builder()
			.product(product)

			.sellerAccount(seller.getAccountNumber())
			.build();
		escrowRepository.save(escrow);

		product.updateEscrow(escrow);
		// 먼저 상품 저장 (FK 필요)
		productRepository.save(product);

		// 태그 저장
		if (req.getTags() != null) {
			List<Tag> tags = new ArrayList<>();
			for (String name : req.getTags()) {
				if (name == null || name.isBlank())
					continue;
				tags.add(Tag.builder()
					.product(product)
					.name(name.trim())
					.build());
			}
			if (!tags.isEmpty())
				tagRepository.saveAll(tags);
		}

		// 이미지 저장
		if (req.getImages() != null) {
			List<Image> images = new ArrayList<>();

			for (int i = 0; i < req.getImages().size(); i++) {
				String url = req.getImages().get(i);
				if (url == null || url.trim().isEmpty())
					continue;

				String finalUrl = url;

				if (i == 0) {
					// 첫 번째 이미지는 오버레이 처리
					finalUrl = productImageOverlayService.processFirstImage(
						url,
						seller.getCompanyName(),
						req.getTitle(),
						product.getTmpSummary()
					);
				}

				images.add(Image.builder()
					.product(product)
					.url(finalUrl)
					.build());
			}

			if (!images.isEmpty()) {
				imageRepository.saveAll(images);
			}
		}

		return new ProductSaveResponse(product.getId());
	}
}