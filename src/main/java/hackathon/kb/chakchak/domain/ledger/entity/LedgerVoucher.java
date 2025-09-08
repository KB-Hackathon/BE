package hackathon.kb.chakchak.domain.ledger.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import hackathon.kb.chakchak.domain.capture.domain.Capture;
import hackathon.kb.chakchak.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "ledger_voucher")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerVoucher extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ledger_voucher_id")
	private Long ledgerVoucherId;

	private String voucherNo;
	private String transactionId;
	private LocalDateTime entryDate;

	@Enumerated(EnumType.STRING)
	private TransactionType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "capture_id")
	private Capture capture;

	@OneToMany(
		mappedBy = "ledgerVoucher",
		fetch = FetchType.LAZY,
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
	@BatchSize(size = 100)
	@Builder.Default
	private List<LedgerEntry> entries = new ArrayList<>();

	@Builder
	public LedgerVoucher(String voucherNo, String transactionId, LocalDateTime entryDate, TransactionType type) {
		this.voucherNo = voucherNo;
		this.transactionId = transactionId;
		this.entryDate = entryDate;
		this.type = type;
	}

	public LedgerEntry addEntry(BigDecimal amount, LedgerType ledgerType, LedgerCode ledgerCode) {
		LedgerEntry entry = LedgerEntry.builder()
			.amount(amount)
			.ledgerType(ledgerType)
			.ledgerVoucher(this)
			.ledgerCode(ledgerCode)
			.build();
		this.entries.add(entry);
		return entry;
	}
}
