package hackathon.kb.chakchak.domain.ledger.batch.reader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import hackathon.kb.chakchak.domain.ledger.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Slf4j
public class IdRangePartitioner implements Partitioner {
	private final LedgerRepository ledgerEntryRepository;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		if (gridSize < 1) gridSize = 1;

		LocalDate yesterday = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
		LocalDateTime start = yesterday.atStartOfDay();
		LocalDateTime end   = start.plusDays(1);

		Long minId = ledgerEntryRepository.findMinIdInRange(start, end);
		Long maxId = ledgerEntryRepository.findMaxIdInRange(start, end);

		log.info("[Partitioner] range={} ~ {}, minId={}, maxId={}", start, end, minId, maxId);

		Map<String, ExecutionContext> result = new HashMap<>();
		if (minId == null || maxId == null) {
			log.warn("[Partitioner] No data for the range. produce 0 partitions.");
			return result; // => 이 경우 partitioned step은 read 0으로 끝남
		}

		long total = maxId - minId + 1;
		long slice = (total + gridSize - 1) / gridSize;

		long startId = minId;
		long endId   = Math.min(startId + slice - 1, maxId);

		for (int i = 0; i < gridSize && startId <= maxId; i++) {
			ExecutionContext ctx = new ExecutionContext();
			ctx.putLong("startId", startId);
			ctx.putLong("endId", endId);
			ctx.putString("startTs", start.toString());
			ctx.putString("endTs", end.toString());
			result.put("partition" + i, ctx);

			log.info("[Partitioner] partition{} => id[{}..{}], ts[{}..{})", i, startId, endId, start, end);

			startId = endId + 1;
			endId   = Math.min(startId + slice - 1, maxId);
		}
		log.info("[Partitioner] produced {} partitions", result.size());
		return result;
	}
}
