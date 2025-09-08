package hackathon.kb.chakchak.domain.product.api.dto;

import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductMetaRequest {
    @Schema(description = "상품명", example = "콜드브루 몰트 크림")
    private String title;

    @Schema(description = "상품 카테고리", example = "카페")
    private Category category;

    @Schema(description = "상품 요약 설명", example = "여름에 딱 맞는 진한 콜드브루와 부드러운 몰트 크림 조합")
    private String summary;

    @Schema(description = "상품 이미지 url", example = "[\"https://chakchak-img/0.png\", \"https://chakchak-img/1.png\"]")
    private List<String> images;

    @Schema(description = "상품 가격", example = "5000")
    private BigDecimal price;

    @Schema(description = "모집 종료일시", example = "2025-09-30T23:59:59")
    private LocalDateTime recruitmentEndPeriod;
}

