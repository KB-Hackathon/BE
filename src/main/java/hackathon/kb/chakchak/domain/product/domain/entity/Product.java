package hackathon.kb.chakchak.domain.product.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import hackathon.kb.chakchak.domain.escrow.domain.entity.Escrow;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus;
import hackathon.kb.chakchak.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	@Column(nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	private ProductStatus status = ProductStatus.PENDING;

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

	public void updateStatus(ProductStatus status) {
		this.status = status;
	}
}
