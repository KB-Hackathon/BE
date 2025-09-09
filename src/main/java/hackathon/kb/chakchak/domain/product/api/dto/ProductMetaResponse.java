package hackathon.kb.chakchak.domain.product.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductMetaResponse(
        @Schema(description = "상품 아이디", example = "1")
        Long productId,

        @Schema(description = "상품 상세 설명 (홍보글)", example = "여름의 시작을 알리는 특별한 순간...")
        String caption,

        @Schema(description = "상품 태그 목록", example = "[\"#스타벅스\", \"#콜드브루\", \"#여름음료\"]")
        List<String> hashtags
) {
}
