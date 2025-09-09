package hackathon.kb.chakchak.domain.product.domain.entity;

import hackathon.kb.chakchak.domain.escrow.domain.entity.Escrow;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus;
import hackathon.kb.chakchak.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private Seller seller;

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	@BatchSize(size = 100)
	private List<Order> orders;

	@OneToMany(fetch = FetchType.LAZY)
	private List<Image> images;

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	@BatchSize(size = 100)
	private List<Tag> tags;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "escrow_id")
	private Escrow escrow;

	@Column(nullable = false)
	private Long endCaptureId;

	@Column(length = 50, nullable = false)
	private String title;

	@Enumerated(EnumType.STRING)
	private Category category;

	@Column(nullable = false)
	private BigDecimal price;

	@Lob
	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String description;

	@Lob
	private String tmpSummary;

	@Enumerated(EnumType.STRING)
	private ProductStatus status;

	private Short targetAmount;

	private boolean isCoupon;

	private String couponName;

	private LocalDateTime couponExpiration;

	@Column(nullable = false)
	private LocalDateTime recruitmentStartPeriod;

	@Column(nullable = false)
	private LocalDateTime recruitmentEndPeriod;

	private Short refreshCnt;

	private LocalDateTime refreshedAt;

	// ====== 변경 메서드 (업데이트 전용) ======

	public void changeEndCaptureId(Long id) {
		this.endCaptureId = id;
	}

	public void changeDescription(String description) {
		if (description != null)
			this.description = description;
	}

	public void changePrice(BigDecimal price) {
		if (price != null)
			this.price = price;
	}

	// boolean 필드는 null-safe 입력 메서드로
	public void changeCoupon(Boolean isCoupon) {
		if (isCoupon != null)
			this.isCoupon = isCoupon;
	}

	public void changeTargetAmount(Short targetAmount) {
		if (targetAmount != null)
			this.targetAmount = targetAmount;
	}

	public void changeRecruitmentPeriods(LocalDateTime start, LocalDateTime end) {
		if (start != null)
			this.recruitmentStartPeriod = start;
		if (end != null)
			this.recruitmentEndPeriod = end;
	}

	public void changeTitle(String title) {
		if (title != null)
			this.title = title;
	}

	public void changeCategory(Category category) {
		if (category != null)
			this.category = category;
	}

	public void changeCouponName(String couponName) {
		if (couponName != null)
			this.couponName = couponName;
	}

	public void changeCouponExpiration(LocalDateTime couponExpiration) {
		if (couponExpiration != null)
			this.couponExpiration = couponExpiration;
	}

	public void markPending() {
		this.status = ProductStatus.PENDING;
	}

	public void touchRefreshedAt(LocalDateTime now) {
		this.refreshedAt = now;
	}

	public void changeTmpSummary(String tmpSummary) {
		if (tmpSummary != null)
			this.tmpSummary = tmpSummary;
	}
}
