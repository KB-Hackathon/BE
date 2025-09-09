package hackathon.kb.chakchak.domain.escrow.domain.entity;

import java.math.BigDecimal;
import java.util.List;

import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "escrow")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Escrow extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "escrow_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	private Product product;

	@OneToMany(mappedBy = "escrow", fetch = FetchType.LAZY)
	private List<EscrowHistory> escrowHistoryList;

	@Builder.Default
	private BigDecimal amount = BigDecimal.ZERO;

	@Builder.Default
	private Boolean isSent = false;

	private String sellerAccount;

	public void plusAmount(BigDecimal amount) {
		this.amount = this.amount.add(amount);
	}
}
