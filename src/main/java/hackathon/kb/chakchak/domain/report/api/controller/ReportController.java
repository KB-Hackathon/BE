package hackathon.kb.chakchak.domain.report.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hackathon.kb.chakchak.domain.report.api.dto.ReportResponseDto;
import hackathon.kb.chakchak.domain.report.service.ReportService;
import hackathon.kb.chakchak.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Tag(name = "리포트 관련 API", description = "개별 판매자 리포트 API")
public class ReportController {

	private final ReportService reportService;

	@Operation(summary = "리포트 조회", description = "판매자 id로 리포트를 조회합니다.")
	@GetMapping("/{sellerId}")
	public BaseResponse<ReportResponseDto> getReport(@PathVariable Long sellerId) {
		return reportService.getReportBySellerId(sellerId);
	}
}
