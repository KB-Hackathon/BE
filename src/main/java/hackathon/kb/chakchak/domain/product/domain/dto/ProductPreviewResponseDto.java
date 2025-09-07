package hackathon.kb.chakchak.domain.product.domain.dto;

import hackathon.kb.chakchak.domain.product.api.dto.ProductProgressResponseDto;
import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ProductPreviewResponseDto(
        @Schema(description = "상품 아이디", example = "1")
        Long productId,
        @Schema(description = "상품명", example = "콜드브루 몰트 크림")
        String title,
        @Schema(description = "상품 이미지")
        List<ImageReadResponseDto> images,
        @Schema(description = "상품 카테고리", example = "의류")
        Category category,
        @Schema(description = "진행 상태", example = "SUCCESS")
        ProductStatus status,
        @Schema(description = "목표 모집 회원 수", example = "30")
        Short targetAmount,
        @Schema(description = "모집 종료 기간", example = "2025-09-01T10:00:00")
        LocalDateTime recruitmentEndPeriod,
        @Schema(description = "참여 인원 + 달성률")
        ProductProgressResponseDto productProgressResponseDto
) {
}
