package hackathon.kb.chakchak.domain.ledger.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hackathon.kb.chakchak.domain.ledger.entity.LedgerCode;
import hackathon.kb.chakchak.domain.ledger.entity.LedgerType;
import hackathon.kb.chakchak.domain.ledger.entity.LedgerVoucher;
import hackathon.kb.chakchak.domain.ledger.entity.TransactionType;
import hackathon.kb.chakchak.domain.ledger.repository.LedgerCodeRepository;
import hackathon.kb.chakchak.domain.ledger.repository.LedgerVoucherRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LedgerService {

	private final LedgerVoucherRepository voucherRepository;
	private final LedgerCodeRepository codeRepository;

	private final Long debitCodeId = 1L;
	private final Long creditCodeId = 2L;

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