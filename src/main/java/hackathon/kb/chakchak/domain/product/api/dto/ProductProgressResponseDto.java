package hackathon.kb.chakchak.domain.product.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductProgressResponseDto(
        @Schema(description = "상품 Id", example = "1")
        Long id,
        @Schema(description = "주문 개수 = 참여 인원", example = "2")
        Long orderCount,
        @Schema(description = "공동 구매 달성률 / 소수 첫자리에서 반올림, 100% 넘어갈 수 있음", example = "80")
        Integer percentAchieved
) {}

