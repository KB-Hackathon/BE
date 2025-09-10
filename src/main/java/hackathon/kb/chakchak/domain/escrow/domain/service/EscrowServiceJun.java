package hackathon.kb.chakchak.domain.escrow.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hackathon.kb.chakchak.domain.escrow.domain.entity.Escrow;
import hackathon.kb.chakchak.domain.escrow.domain.repository.EscrowRepositoryJun;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EscrowServiceJun {

	private final EscrowRepositoryJun escrowRepositoryJun;

	public BigDecimal sumOfPriceYesterday(){
		LocalDate yesterday = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
		List<Escrow> allByCreatedDate = escrowRepositoryJun.findAllByCreatedDate(yesterday);

		BigDecimal sumOfAmount = BigDecimal.ZERO;

		for (Escrow escrow : allByCreatedDate) {
			sumOfAmount = sumOfAmount.add(escrow.getAmount());
		}
		return sumOfAmount;
	}
}
