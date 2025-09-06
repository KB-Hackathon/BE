package hackathon.kb.chakchak.domain.order.domain.dto;

import hackathon.kb.chakchak.domain.order.domain.enums.OrderStatus;
import hackathon.kb.chakchak.domain.product.domain.dto.ProductPreviewResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record OrderItemResponseDto(
        @Schema(description = "주문 아이디", example = "10")
        Long orderId,
        @Schema(description = "주문 수량", example = "1")
        Short quantity,
        @Schema(description = "발송 여부", example = "true")
        Boolean isSent,
        @Schema(description = "운송장번호", example = "123456789")
        String deliveryCode,
        @Schema(description = "주문 상태", example = "PAY_COMPLETE")
        OrderStatus orderStatus,
        @Schema(description = "상품 요약 정보")
        ProductPreviewResponseDto product
) {}