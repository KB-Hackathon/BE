package hackathon.kb.chakchak.domain.ledger.batch.writer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import hackathon.kb.chakchak.domain.escrow.domain.service.EscrowServiceJun;
import hackathon.kb.chakchak.domain.ledger.entity.LedgerVoucher;
import hackathon.kb.chakchak.domain.ledger.entity.TransactionType;
import hackathon.kb.chakchak.domain.ledger.service.LedgerService;
import hackathon.kb.chakchak.domain.order.service.OrderService;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.kafka.dto.Common;
import hackathon.kb.chakchak.global.kafka.dto.LogLevel;
import hackathon.kb.chakchak.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LedgerWriter {

	private final OrderService orderService;
	private final LedgerService ledgerService;
	private final EscrowServiceJun escrowServiceJun;

	// 동일 Step 실행 동안 이미 처리한 바우처 ID를 기억(청크 간 중복 방지)
	// @StepScope 덕분에 파티션/스텝마다 별도 인스턴스가 생깁니다.
	private final Set<Long> seenVoucherIds = new HashSet<>();

	@Bean
	@StepScope
	public ItemWriter<LedgerVoucher> LedgerItemWriter() {
		// Spring Batch 5: write 파라미터는 Chunk<? extends T>
		return (Chunk<? extends LedgerVoucher> chunk) -> {
			int rawSize = chunk.getItems().size();

			// 1) 청크 내부 중복 제거 + 2) 스텝 전체 중복 제거
			List<LedgerVoucher> unique = new ArrayList<>();
			for (LedgerVoucher v : chunk) {
				if (v == null) continue;
				Long id = v.getLedgerVoucherId();
				// ID가 없을 일은 거의 없겠지만, 방어적으로 처리
				if (id == null || seenVoucherIds.add(id)) {
					unique.add(v);
				}
			}

			log.debug("Writer received: rawSize={}, uniqueSize={}", rawSize, unique.size());

			// 이후 로직은 유니크 바우처만 사용
			checkOfWithdrawBalanced(unique);
			checkOfTransferBalanced(unique);
		};
	}

	private void checkOfTransferBalanced(List<LedgerVoucher> vouchers) {
		BigDecimal sumOfOrders       = orderService.priceSumOfYesterdayOrders();
		BigDecimal sumOfLedgerDebit  = ledgerService.getSumOfDebitByTransactionType(vouchers, TransactionType.TRANSFER);
		BigDecimal sumOfLedgerCredit = ledgerService.getSumOfCreditByTransactionType(vouchers, TransactionType.TRANSFER);

		boolean isMatch =
			sumOfOrders.compareTo(sumOfLedgerDebit) == 0 &&
				sumOfOrders.compareTo(sumOfLedgerCredit) == 0 &&
				sumOfLedgerDebit.compareTo(sumOfLedgerCredit) == 0;

		if (!isMatch) {
			log.error("TRANSFER 정합성 검증 실패: orders={}, debit={}, credit={}",
				sumOfOrders, sumOfLedgerDebit, sumOfLedgerCredit);
			throw new BusinessException(ResponseCode.LEDGER_VERIFY_FAIL, LogLevel.ERROR, null, Common.builder().build());
		} else {
			log.info("TRANSFER 정합성 검증 성공: orders={}, debit={}, credit={}",
				sumOfOrders, sumOfLedgerDebit, sumOfLedgerCredit);
		}
	}

	private void checkOfWithdrawBalanced(List<LedgerVoucher> vouchers) {
		BigDecimal sumOfEscrow       = escrowServiceJun.sumOfPriceYesterday();
		BigDecimal sumOfLedgerDebit  = ledgerService.getSumOfDebitByTransactionType(vouchers, TransactionType.WITHDRAW);
		BigDecimal sumOfLedgerCredit = ledgerService.getSumOfCreditByTransactionType(vouchers, TransactionType.WITHDRAW);

		boolean isMatch =
			sumOfEscrow.compareTo(sumOfLedgerDebit) == 0 &&
				sumOfEscrow.compareTo(sumOfLedgerCredit) == 0 &&
				sumOfLedgerDebit.compareTo(sumOfLedgerCredit) == 0;

		if (!isMatch) {
			log.error("WITHDRAW 정합성 검증 실패: escrow={}, debit={}, credit={}",
				sumOfEscrow, sumOfLedgerDebit, sumOfLedgerCredit);
			throw new BusinessException(ResponseCode.LEDGER_VERIFY_FAIL, LogLevel.ERROR, null, Common.builder().build());
		} else {
			log.info("WITHDRAW 정합성 검증 성공: escrow={}, debit={}, credit={}",
				sumOfEscrow, sumOfLedgerDebit, sumOfLedgerCredit);
		}
	}
}
