package hackathon.kb.chakchak.domain.order.domain.entity;

import hackathon.kb.chakchak.domain.order.domain.enums.OrderStatus;
import hackathon.kb.chakchak.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "order")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	@Column(nullable = false)
	private Short quantity;

	@Column(nullable = false)
	private String transactionId;

	private Boolean isSent;

	@Column(length = 25)
	private String deliveryCode;

	@Column(length = 5)
	private String agency;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

}
