package hackathon.kb.chakchak.domain.product.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NarrativeResult {
    @Schema(description = "상품 ID", example = "12")
    private final Long productId;

    @Schema(description = "상품 상세 설명 (홍보글)", example = "여름의 시작을 알리는 특별한 순간...")
    private final String caption;

    @Schema(description = "상품 태그 목록", example = "[\"#스타벅스\", \"#콜드브루\", \"#여름음료\"]")
    private final List<String> hashtags;
}
