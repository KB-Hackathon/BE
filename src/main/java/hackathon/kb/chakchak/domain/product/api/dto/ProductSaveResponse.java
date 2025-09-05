package hackathon.kb.chakchak.domain.product.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductSaveResponse(
        @Schema(description = "상품 아이디", example = "1")
        Long productId) {
}
