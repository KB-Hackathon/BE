package hackathon.kb.chakchak.domain.report.api.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportResponseDto {

	@Schema(description = "총 매출액")
	private Integer totalSales;

	@Schema(description = "성공 횟수")
	private Integer successCnt;

	@Schema(description = "실패 횟수")
	private Integer failedCnt;

	@Schema(description = "성공률 (%)")
	private Integer successRate;

	@Schema(description = "연령대 분포 (%)")
	private ReportAgeDistributionResponse ageDistribution;

	@Schema(description = "성별 분포 (%)")
	private ReportGenderDistributionResponse genderDistribution;
}
