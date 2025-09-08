package hackathon.kb.chakchak.domain.report.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReportGenderDistributionResponse(
        @Schema(description = "남성", example = "15")
        Integer maleCnt,
        @Schema(description = "여성", example = "20")
        Integer femaleCnt
) {}