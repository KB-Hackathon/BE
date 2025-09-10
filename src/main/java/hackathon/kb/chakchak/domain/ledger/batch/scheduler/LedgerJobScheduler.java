package hackathon.kb.chakchak.domain.ledger.batch.scheduler;

import java.util.Set;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LedgerJobScheduler {

	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;
	private final Job ledgerJob;

	public LedgerJobScheduler(
		JobLauncher jobLauncher,
		JobExplorer jobExplorer,
		Job ledgerJob
	) {
		this.jobLauncher = jobLauncher;
		this.jobExplorer = jobExplorer;
		this.ledgerJob = ledgerJob;
	}

	@Scheduled(cron = "0 43 13 * * *", zone = "Asia/Seoul")
	@SchedulerLock(name = "ledgerJobLock", lockAtMostFor = "PT30M", lockAtLeastFor = "PT1M")
	public void runOnSchedule() {
		try {
			if (isRunning(ledgerJob.getName())) {
				log.warn("[ledgerJob] 이미 실행 중이어서 이번 스케줄은 스킵합니다.");
				return;
			}

			var params = new JobParametersBuilder()
				.addLong("run.id", System.currentTimeMillis()) // 재실행 구분용
				.addString("trigger", "scheduler")
				.toJobParameters();

			JobExecution exec = jobLauncher.run(ledgerJob, params);
			log.info("[ledgerJob] 실행 시작 - executionId={}, status={}", exec.getId(), exec.getStatus());

		} catch (Exception e) {
			log.error("[ledgerJob] 실행 실패", e);
		}
	}

	private boolean isRunning(String jobName) {
		Set<JobExecution> running = jobExplorer.findRunningJobExecutions(jobName);
		return running != null && !running.isEmpty();
	}
}
