package hackathon.kb.chakchak.domain.product.domain.dto;

import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductSimpleResponseDto(
        @Schema(description = "상품 아이디", example = "1")
        Long productId,
        @Schema(description = "상품명", example = "콜드브루 몰트 크림")
        String title,
        @Schema(description = "상품 이미지")
        List<ImageReadResponseDto> images,
        @Schema(description = "상품 해시태그")
        List<TagReadResponseDto> tags,
        @Schema(description = "상품 카테고리", example = "의류")
        Category category,
        @Schema(description = "상품 가격", example = "1000000")
        BigDecimal price,
        @Schema(description = "상품 설명", example = "이쁜 옷")
        String description,
        @Schema(description = "쿠폰 사용 가능 여부", example = "true")
        Boolean isCoupon,
        @Schema(description = "목표 모집 회원 수", example = "30")
        Short targetAmount,
        @Schema(description = "참여중인 인원", example = "22")
        Short presentPersonCount,
        @Schema(description = "현재 달성 액수", example = "34200000")
        BigDecimal totalPrice,
        @Schema(description = "모집 시작 기간", example = "2025-09-01T10:00:00")
        LocalDateTime recruitmentStartPeriod,
        @Schema(description = "모집 종료 기간", example = "2025-09-01T10:00:00")
        LocalDateTime recruitmentEndPeriod
) {
}
