package hackathon.kb.chakchak.domain.product.domain.entity;

import org.hibernate.annotations.BatchSize;

import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus;
import hackathon.kb.chakchak.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	@BatchSize(size = 100)
	private List<Image> images;

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	@BatchSize(size = 100)
	private List<Tag> tags;

	@Column(nullable = false)
	private Long endCaptureId;

	@Column(length = 50, nullable = false)
	private String title;

	@Enumerated(EnumType.STRING)
	private Category category;

	@Column(nullable = false)
	private Long price;

	@Lob
	@Column(columnDefinition = "LONGTEXT", nullable = false)
	private String description;

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
}
