package hackathon.kb.chakchak.domain.order.domain.entity;

import java.math.BigDecimal;

import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;
import hackathon.kb.chakchak.domain.order.domain.enums.OrderStatus;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
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
import jakarta.persistence.ManyToOne;
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
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "buyer_id")
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
	private Coupon coupon;

	@Column(nullable = false)
	private Short quantity;

	@Column(nullable = false)
	private String transactionId;

	private BigDecimal price;

	private Boolean isSent;

	@Column(length = 25)
	private String deliveryCode;

	@Column(length = 5)
	private String agency;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

}
