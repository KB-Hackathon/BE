package hackathon.kb.chakchak.domain.product.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ProductReadResponseDto(
	@Schema(description = "판매자 정보")
	ProductMemberReadResponseDto seller,

	@Schema(description = "상품 정보")
	ProductSimpleResponseDto product
) {
}
