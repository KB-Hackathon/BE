package hackathon.kb.chakchak.domain.report.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Service;

import hackathon.kb.chakchak.domain.report.api.dto.ReportResponseDto;
import hackathon.kb.chakchak.domain.report.domain.entity.Report;
import hackathon.kb.chakchak.domain.report.repository.ReportRepository;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.BaseResponse;
import hackathon.kb.chakchak.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;

	public ReportResponseDto getReportBySellerId(Long sellerId) {
		Report report = reportRepository.findBySellerId(sellerId)
			.orElseThrow(() -> new BusinessException(ResponseCode.REPORT_NOT_FOUND));

		// 1. 성공률 계산
		int successRate = calcSuccessRate(report.getSuccessCnt(), report.getFailedCnt());

		// 2. 연령대 분포 계산
		Map<String, Integer> ageDistribution = calcAgeDistribution(report);

		// 3. 성별 분포 계산
		Map<String, Integer> genderDistribution = calcGenderDistribution(report);

		return ReportResponseDto.builder()
			.totalSales(report.getTotalSales())
			.successCnt(report.getSuccessCnt())
			.failedCnt(report.getFailedCnt())
			.successRate(successRate)
			.ageDistribution(ageDistribution)
			.genderDistribution(genderDistribution)
			.build();
	}

	private int calcSuccessRate(int success, int fail) {
		int total = success + fail;
		if (total == 0) return 0;
		double raw = success * 100.0 / total;
		return (int) Math.round(raw);
	}

	/**
	 * 합계 = 100
	 * 보정치는 가장 큰 값에 반영
	 */
	private Map<String, Integer> calcAgeDistribution(Report report) {
		int over10 = nullToZero(report.getOver10());
		int over20 = nullToZero(report.getOver20());
		int over30 = nullToZero(report.getOver30());
		int over40 = nullToZero(report.getOver40());
		int over50 = nullToZero(report.getOver50());
		int over60 = nullToZero(report.getOver60());

		int sum = over10 + over20 + over30 + over40 + over50 + over60;
		if (sum == 0) return Map.of("데이터 없음", 100);

		int ten    = (int) Math.round(over10 * 100.0 / sum);
		int twenty = (int) Math.round(over20 * 100.0 / sum);
		int thirty = (int) Math.round(over30 * 100.0 / sum);
		int forty  = (int) Math.round(over40 * 100.0 / sum);
		int fifty  = (int) Math.round(over50 * 100.0 / sum);
		int sixty  = (int) Math.round(over60 * 100.0 / sum);

		int total = ten + twenty + thirty + forty + fifty + sixty;
		int diff = 100 - total;

		// 최대값 찾기 (여러 개면 첫 번째 항목)
		int maxValue = Collections.max(Arrays.asList(ten, twenty, thirty, forty, fifty, sixty));
		if (maxValue == ten) ten += diff;
		else if (maxValue == twenty) twenty += diff;
		else if (maxValue == thirty) thirty += diff;
		else if (maxValue == forty) forty += diff;
		else if (maxValue == fifty) fifty += diff;
		else sixty += diff;

		return Map.of(
			"10대", ten,
			"20대", twenty,
			"30대", thirty,
			"40대", forty,
			"50대", fifty,
			"60대 이상", sixty
		);
	}

	private Map<String, Integer> calcGenderDistribution(Report r) {
		int sum = nullToZero(r.getMaleCnt()) + nullToZero(r.getFemaleCnt());
		if (sum == 0) return Map.of("데이터 없음", 100);

		int male = (int) Math.round(nullToZero(r.getMaleCnt()) * 100.0 / sum);
		int female = (int) Math.round(nullToZero(r.getFemaleCnt()) * 100.0 / sum);

		int total = male + female;
		int diff = 100 - total;

		if (male >= female) male += diff;
		else female += diff;

		return Map.of(
			"남성", male,
			"여성", female
		);
	}

	private int nullToZero(Integer v) { return v == null ? 0 : v; }
}
