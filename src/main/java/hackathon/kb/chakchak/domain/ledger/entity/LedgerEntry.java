package hackathon.kb.chakchak.domain.ledger.entity;

import java.math.BigDecimal;

import hackathon.kb.chakchak.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "ledger_entry")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerEntry extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ledger_entry_id")
	private Long ledgerEntryId;
	private BigDecimal amount;
	private LedgerType ledgerType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ledger_voucher_id")
	private LedgerVoucher ledgerVoucher;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ledger_code_id")
	private LedgerCode ledgerCode;

	@Builder
	public LedgerEntry(BigDecimal amount, LedgerType ledgerType, LedgerVoucher ledgerVoucher, LedgerCode ledgerCode) {
		this.amount = amount;
		this.ledgerType = ledgerType;
		this.ledgerVoucher = ledgerVoucher;
		this.ledgerCode = ledgerCode;
	}
}
