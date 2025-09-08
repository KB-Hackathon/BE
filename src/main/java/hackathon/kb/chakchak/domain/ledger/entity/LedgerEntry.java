package hackathon.kb.chakchak.domain.ledger.entity;

import java.math.BigDecimal;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "ledger_entry")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class LedgerEntry extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ledger_entry_id")
	private Long ledgerEntryId;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "ledger_type", nullable = false, length = 10)
	private LedgerType ledgerType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ledger_voucher_id", nullable = false)
	private LedgerVoucher ledgerVoucher;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ledger_code_id", nullable = false)
	private LedgerCode ledgerCode;
}