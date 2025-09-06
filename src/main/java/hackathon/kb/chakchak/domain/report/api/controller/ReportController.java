package hackathon.kb.chakchak.domain.report.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hackathon.kb.chakchak.domain.report.api.dto.ReportResponseDto;
import hackathon.kb.chakchak.domain.report.service.ReportService;
import hackathon.kb.chakchak.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sellers/report")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@GetMapping("/{sellerId}")
	public BaseResponse<ReportResponseDto> getReport(@PathVariable Long sellerId) {
		return reportService.getReportBySellerId(sellerId);
	}
}
