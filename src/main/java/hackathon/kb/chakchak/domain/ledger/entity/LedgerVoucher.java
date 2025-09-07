package hackathon.kb.chakchak.domain.ledger.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import hackathon.kb.chakchak.domain.capture.domain.Capture;
import hackathon.kb.chakchak.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	private TransactionType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "capture_id")
	private Capture capture;

	@OneToMany(mappedBy = "ledgerVoucher", fetch = FetchType.LAZY)
	@BatchSize(size = 100)
	private List<LedgerEntry> entries;

	@Builder
	public LedgerVoucher(String voucherNo, String transactionId, LocalDateTime entryDate, TransactionType type) {
		this.voucherNo = voucherNo;
		this.transactionId = transactionId;
		this.entryDate = entryDate;
		this.type = type;
	}
}
