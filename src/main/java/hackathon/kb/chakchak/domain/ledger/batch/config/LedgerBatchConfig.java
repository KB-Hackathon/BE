package hackathon.kb.chakchak.domain.ledger.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import hackathon.kb.chakchak.domain.ledger.batch.reader.IdRangePartitioner;
import hackathon.kb.chakchak.domain.ledger.entity.LedgerEntry;
import hackathon.kb.chakchak.domain.ledger.entity.LedgerVoucher;
import hackathon.kb.chakchak.domain.ledger.repository.LedgerRepository;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LedgerBatchConfig extends DefaultBatchConfiguration {

	private final ItemWriter<LedgerVoucher> ledgerWriter;
	private final LedgerRepository ledgerRepository;
	private final JpaPagingItemReader<LedgerEntry> ledgerReader;
	private final ItemProcessor<LedgerEntry, LedgerVoucher> ledgerProcessor;

	private final int threadSize = 1;

	@Bean
	public Job emailNotificationJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new JobBuilder("ledgerJob", jobRepository)
			.start(partitionedEmailStep(jobRepository, transactionManager))
			.build();
	}

	@Bean
	public Step partitionedEmailStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("partitionedLedgerStep", jobRepository)
			.partitioner("ledgerStep", new IdRangePartitioner(ledgerRepository))
			.gridSize(threadSize)
			.step(emailNotificationStep(jobRepository, transactionManager))
			.taskExecutor(taskExecutor(threadSize))
			.build();
	}

	@Bean
	public Step emailNotificationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("ledgerStep", jobRepository)
			.<LedgerEntry, LedgerVoucher>chunk(1000, transactionManager)
			.reader(ledgerReader)
			.processor(ledgerProcessor)
			.writer(ledgerWriter)
			.build();
	}

	public TaskExecutor taskExecutor(int threadPoolSize) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(threadPoolSize);
		executor.setMaxPoolSize(threadPoolSize);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("batch-thread-");
		executor.initialize();
		return executor;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
