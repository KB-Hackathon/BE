package hackathon.kb.chakchak.domain.ledger.batch.processor;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hackathon.kb.chakchak.domain.ledger.entity.LedgerEntry;
import hackathon.kb.chakchak.domain.ledger.entity.LedgerVoucher;

@Configuration
public class LedgerProcessorConfig {

	@Bean
	@StepScope
	public ItemProcessor<LedgerEntry, LedgerVoucher> ledgerProcessor() {
		return entry -> {
			if (entry == null) return null; // null이면 필터링
			// reader 에서 join fetch로 미리 로딩했으니 바로 꺼냄
			LedgerVoucher voucher = entry.getLedgerVoucher();
			// 필요 시 null 체크해서 스킵 또는 예외
			// if (voucher == null) return null; // 스킵
			return voucher;
		};
	}
}
