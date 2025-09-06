package hackathon.kb.chakchak.domain.report.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import hackathon.kb.chakchak.domain.report.api.dto.ReportResponseDto;
import hackathon.kb.chakchak.domain.report.domain.entity.Report;
import hackathon.kb.chakchak.domain.report.repository.ReportRepository;
import hackathon.kb.chakchak.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;

	public BaseResponse<ReportResponseDto> getReportBySellerId(Long sellerId) {
		Report report = reportRepository.findBySellerId(sellerId)
			.orElseThrow(() -> new IllegalArgumentException("리포트가 존재하지 않습니다."));

		// 1. 성공률 계산
		double successRate = calcSuccessRate(report.getSuccessCnt(), report.getFailedCnt());

		// 2. 연령대 분포 계산
		Map<String, Double> ageDistribution = calcAgeDistribution(report);

		// 3. 성별 분포 계산
		Map<String, Double> genderDistribution = calcGenderDistribution(report);

		ReportResponseDto responseDto = ReportResponseDto.builder()
			.totalSales(report.getTotalSales())
			.successCnt(report.getSuccessCnt())
			.failedCnt(report.getFailedCnt())
			.successRate(successRate)
			.ageDistribution(ageDistribution)
			.genderDistribution(genderDistribution)
			.build();

		return BaseResponse.OK(responseDto);
	}

	private double calcSuccessRate(int success, int fail) {
		int total = success + fail;
		return total == 0 ? 0.0 : (success * 100.0 / total);
	}

	private Map<String, Double> calcAgeDistribution(Report report) {
		int sum = report.getOver10() + report.getOver20() + report.getOver30() +
			report.getOver40() + report.getOver50() + report.getOver60();
		if (sum == 0) return Map.of();

		return Map.of(
			"10대", report.getOver10() * 100.0 / sum,
			"20대", report.getOver20() * 100.0 / sum,
			"30대", report.getOver30() * 100.0 / sum,
			"40대", report.getOver40() * 100.0 / sum,
			"50대", report.getOver50() * 100.0 / sum,
			"60대 이상", report.getOver60() * 100.0 / sum
		);
	}

	private Map<String, Double> calcGenderDistribution(Report report) {
		int sum = report.getMaleCnt() + report.getFemaleCnt();
		if (sum == 0)
			return Map.of();

		return Map.of(
			"남성", report.getMaleCnt() * 100.0 / sum,
			"여성", report.getFemaleCnt() * 100.0 / sum
		);
	}
}
