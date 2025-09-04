package hackathon.kb.chakchak.domain.product.api.dto;

import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProductMetaRequest {
    @Schema(description = "상품명", example = "콜드브루 몰트 크림")
    private String title;

    @Schema(description = "상품 카테고리", example = "카페")
    private Category category;

    @Schema(description = "상품 요약 설명", example = "여름에 딱 맞는 진한 콜드브루와 부드러운 몰트 크림 조합")
    private String summary;
}

