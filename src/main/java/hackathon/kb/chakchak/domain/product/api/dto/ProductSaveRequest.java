package hackathon.kb.chakchak.domain.product.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductSaveRequest {
    @Schema(description = "상품 ID (기존 상품 식별자)", example = "12")
    private Long productId;

    @Schema(description = "상품 상세 설명", example = "여름의 시작을 알리는 특별한 순간...")
    private String description;

    @Schema(description = "상품 태그 목록", example = "[\"#스타벅스\", \"#콜드브루\", \"#여름음료\"]")
    private List<String> tags;

    @Schema(description = "상품 가격", example = "4800")
    private Long price;

    @Schema(description = "쿠폰 사용 가능 여부", example = "true")
    private Boolean isCoupon;

    @Schema(description = "목표 수량", example = "100")
    private Short targetAmount;

    @Schema(description = "모집 시작일시", example = "2025-09-05T00:00:00")
    private LocalDateTime recruitmentStartPeriod;

    @Schema(description = "모집 종료일시", example = "2025-09-30T23:59:59")
    private LocalDateTime recruitmentEndPeriod;
}

