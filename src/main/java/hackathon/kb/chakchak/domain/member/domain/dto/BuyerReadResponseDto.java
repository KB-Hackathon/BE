package hackathon.kb.chakchak.domain.member.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record BuyerReadResponseDto(
        @Schema(description = "구매자 아이디", example = "1")
        Long memberId,
        @Schema(description = "이름", example = "홍길동")
        String name
) {}
