package hackathon.kb.chakchak.domain.member.api.dto.res;

import hackathon.kb.chakchak.domain.product.domain.dto.ProductMemberReadResponseDto;
import hackathon.kb.chakchak.domain.product.domain.dto.ProductSimpleResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record SellerReadResponseDto(
        @Schema(description = "판매자 정보")
        ProductMemberReadResponseDto seller,

        @Schema(description = "판매자가 등록한 상품 리스트")
        List<ProductSimpleResponseDto> products
) {
}
