package hackathon.kb.chakchak.domain.report.api.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportResponseDto {
	private Integer totalSales;     // 총 매출액
	private Integer successCnt;     // 성공 횟수
	private Integer failedCnt;      // 실패 횟수
	private Integer successRate;     // 성공률 (%)

	private Map<String, Integer> ageDistribution;   // 연령대 분포 (%)
	private Map<String, Integer> genderDistribution; // 성별 분포 (%)
}
