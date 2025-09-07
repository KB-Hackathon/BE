package hackathon.kb.chakchak.domain.order.domain.entity;

import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.order.domain.enums.OrderStatus;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
	private Member buyer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
	private Coupon coupon;

	@Column(nullable = false)
	private Short quantity;

	// Todo: ledger 생성 후 추가
	// @Column(nullable = false)
	// private String transactionId;

	private Boolean isSent;

	@Column(length = 25)
	private String deliveryCode;

	@Column(length = 5)
	private String agency;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

}
