package hackathon.kb.chakchak.domain.ledger.batch.reader;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hackathon.kb.chakchak.domain.ledger.entity.LedgerEntry;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LedgerReader {

	@Bean
	@StepScope
	public JpaPagingItemReader<LedgerEntry> ledgerEntryReader(
		@Value("#{stepExecutionContext['startId']}") Long startId,
		@Value("#{stepExecutionContext['endId']}") Long endId,
		@Value("#{stepExecutionContext['startTs']}") String startTs,
		@Value("#{stepExecutionContext['endTs']}") String endTs,
		EntityManagerFactory emf
	) {
		LocalDateTime start = LocalDateTime.parse(startTs);
		LocalDateTime end   = LocalDateTime.parse(endTs);

		JpaPagingItemReader<LedgerEntry> reader = new JpaPagingItemReader<>();
		reader.setName("ledgerEntryReader");
		reader.setEntityManagerFactory(emf);
		reader.setPageSize(1000);
		reader.setSaveState(true);

		reader.setQueryString(
			"select le " +
				"from LedgerEntry le " +
				"join fetch le.ledgerVoucher v " +
				"where le.ledgerEntryId between :startId and :endId " +
				"  and le.createdAt >= :start and le.createdAt < :end " +
				"order by le.ledgerEntryId asc"
		);

		Map<String, Object> params = new HashMap<>();
		params.put("startId", startId);
		params.put("endId", endId);
		params.put("start", start);
		params.put("end", end);
		reader.setParameterValues(params);

		return reader;
	}
}
