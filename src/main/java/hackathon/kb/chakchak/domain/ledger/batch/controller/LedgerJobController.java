package hackathon.kb.chakchak.domain.ledger.batch.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/batch/ledger")
@RequiredArgsConstructor
@Tag(name = "차대변 테스트 api", description = "차대변 정합성 배치 트리거 API (테스트용)")
@Slf4j
public class LedgerJobController {

	private final JobLauncher jobLauncher;
	private final Job ledgerJob;

	@GetMapping("/run")
	public ResponseEntity<Map<String, Object>> run() throws Exception {
		var params = new JobParametersBuilder()
			.addLong("run.id", System.currentTimeMillis())
			.toJobParameters();

		JobExecution execution = jobLauncher.run(ledgerJob, params);

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("jobName", execution.getJobInstance().getJobName());
		body.put("executionId", execution.getId());
		body.put("status", execution.getStatus().toString());
		body.put("exitStatus", execution.getExitStatus().getExitCode());
		body.put("steps", execution.getStepExecutions().stream().map(this::toStepSummary).collect(Collectors.toList()));

		return ResponseEntity.ok(body);
	}

	private Map<String, Object> toStepSummary(StepExecution se) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("stepName", se.getStepName());
		m.put("status", se.getStatus().toString());
		m.put("readCount", se.getReadCount());
		m.put("writeCount", se.getWriteCount());
		m.put("commitCount", se.getCommitCount());
		m.put("rollbackCount", se.getRollbackCount());
		m.put("readSkipCount", se.getReadSkipCount());
		m.put("processSkipCount", se.getProcessSkipCount());
		m.put("filterCount", se.getFilterCount());
		m.put("writeSkipCount", se.getWriteSkipCount());
		return m;
	}
}