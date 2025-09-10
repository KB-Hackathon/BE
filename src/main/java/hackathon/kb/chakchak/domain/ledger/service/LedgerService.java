package hackathon.kb.chakchak.domain.ledger.service;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hackathon.kb.chakchak.domain.ledger.entity.LedgerEntry;
import hackathon.kb.chakchak.domain.ledger.entity.LedgerCode;
import hackathon.kb.chakchak.domain.ledger.entity.LedgerType;
import hackathon.kb.chakchak.domain.ledger.entity.LedgerVoucher;
import hackathon.kb.chakchak.domain.ledger.entity.TransactionType;
import hackathon.kb.chakchak.domain.ledger.repository.LedgerRepository;
import hackathon.kb.chakchak.domain.ledger.repository.LedgerCodeRepository;
import hackathon.kb.chakchak.domain.ledger.repository.LedgerVoucherRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LedgerService {

	private final LedgerRepository ledgerRepository;
	private final LedgerVoucherRepository voucherRepository;
	private final LedgerCodeRepository codeRepository;

	private final Long debitCodeId = 1L;
	private final Long creditCodeId = 2L;

	public BigDecimal getSumOfDebitByTransactionType(List<LedgerVoucher> vouchers, TransactionType transactionType) {
		BigDecimal sumOfDebit = BigDecimal.ZERO;
		System.out.println("vouchers size = " + vouchers.size());
		for (LedgerVoucher voucher : vouchers) {
			if (voucher.getType().equals(transactionType)) {
				List<LedgerEntry> entries = voucher.getEntries();
				for (LedgerEntry entry : entries) {
					if (entry.getLedgerType().equals(LedgerType.DEBIT)) {
						System.out.println("entry.getLedgerEntryId() = " + entry.getLedgerEntryId());
						sumOfDebit = sumOfDebit.add(entry.getAmount());
					}
				}
			}
		}
		return sumOfDebit;
	}

	public BigDecimal getSumOfCreditByTransactionType(List<LedgerVoucher> vouchers, TransactionType transactionType) {
		BigDecimal sumOfDebit = BigDecimal.ZERO;
		for (LedgerVoucher voucher : vouchers) {
			if(voucher.getType().equals(transactionType)) {
				List<LedgerEntry> entries = voucher.getEntries();
				for (LedgerEntry entry : entries) {
					if (entry.getLedgerType().equals(LedgerType.CREDIT)) {
						sumOfDebit = sumOfDebit.add(entry.getAmount());
					}
				}
			}
		}
		return sumOfDebit;
	}


	/**
	 * 전표에 대해서 이중분기 회계 저장
	 * @param transactionId
	 * @param transactionType
	 * @param amount
	 * @return
	 */
	@Transactional
	public LedgerVoucher createAndSaveVoucherWithDoubleEntry(
		String transactionId,
		TransactionType transactionType,
		BigDecimal amount
	) {

		LedgerCode debitCode = codeRepository.getReferenceById(debitCodeId);
		LedgerCode creditCode = codeRepository.getReferenceById(creditCodeId);

		LedgerVoucher voucher = LedgerVoucher.builder()
			.voucherNo(generateVoucherNo())
			.transactionId(transactionId)
			.entryDate(LocalDateTime.now())
			.type(transactionType)
			.build();

		voucher.addEntry(amount, LedgerType.DEBIT, debitCode);
		voucher.addEntry(amount, LedgerType.CREDIT, creditCode);

		return voucherRepository.save(voucher);
	}

	private String generateVoucherNo() {
		String no;
		do {
			no = "V-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
		} while (voucherRepository.existsByVoucherNo(no));
		return no;
	}


}
