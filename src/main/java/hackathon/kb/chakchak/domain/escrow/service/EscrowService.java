package hackathon.kb.chakchak.domain.escrow.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hackathon.kb.chakchak.domain.escrow.domain.entity.Escrow;
import hackathon.kb.chakchak.domain.escrow.domain.entity.EscrowHistory;
import hackathon.kb.chakchak.domain.escrow.repository.EscrowHistoryRepository;
import hackathon.kb.chakchak.domain.escrow.repository.EscrowRepository;
import hackathon.kb.chakchak.domain.ledger.entity.TransactionType;
import hackathon.kb.chakchak.domain.ledger.service.LedgerService;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EscrowService {

	private final EscrowRepository escrowRepository;
	private final EscrowHistoryRepository escrowHistoryRepository;
	private final LedgerService ledgerService;

	/**
	 * 에스크로 계좌 구매완료 후 출금 로직
	 * @param product
	 */
	@Transactional
	public void terminateBuy(Product product) {

		Escrow escrow = product.getEscrow();

		BigDecimal price = escrow.getAmount();

		List<EscrowHistory> histories = new ArrayList<>();
		for (Order order : product.getOrders()) {
			price.subtract(order.getPrice());

			String transactionId = UUID.randomUUID().toString().replace("-", "");
			ledgerService.createAndSaveVoucherWithDoubleEntry(transactionId, TransactionType.WITHDRAW,
				order.getPrice());

			EscrowHistory escrowHistory = EscrowHistory.builder()
				.escrow(escrow)
				.amount(order.getPrice())
				.transactionId(transactionId)
				.buyerAccount(order.getBuyer().getPhoneNumber())
				.build();

			histories.add(escrowHistory);
		}

		escrowHistoryRepository.saveAll(histories);
	}

}
