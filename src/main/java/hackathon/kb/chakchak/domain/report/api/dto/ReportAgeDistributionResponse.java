package hackathon.kb.chakchak.domain.report.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReportAgeDistributionResponse(
        @Schema(description = "10대 이상", example = "15")
        Integer over10,
        @Schema(description = "20대 이상", example = "20")
        Integer over20,
        @Schema(description = "30대 이상", example = "10")
        Integer over30,
        @Schema(description = "40대 이상", example = "25")
        Integer over40,
        @Schema(description = "50대 이상", example = "20")
        Integer over50,
        @Schema(description = "60대 이상", example = "10")
        Integer over60
) {}