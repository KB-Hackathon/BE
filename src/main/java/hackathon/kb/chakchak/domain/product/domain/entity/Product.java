package hackathon.kb.chakchak.domain.product.domain.entity;

import java.time.LocalDateTime;

import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
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
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private Seller seller;

	@Column(nullable = false)
	private Long endCaptureId;

	@Column(length = 50, nullable = false)
	private String title;

	@Enumerated(EnumType.STRING)
	private Category category; // DB ENUM 매핑(값은 프로젝트 상황에 맞게 조정)

	@Column(nullable = false)
	private Long price;

	@Lob
	@Column(nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	private ProductStatus status;

	private Short targetAmount;

	@Column(nullable = false)
	private LocalDateTime recruitmentStartPeriod;

	@Column(nullable = false)
	private LocalDateTime recruitmentEndPeriod;

	private Short refreshCnt;

	private LocalDateTime refreshedAt;
}
