package hackathon.kb.chakchak.domain.order.util;

import hackathon.kb.chakchak.domain.order.domain.dto.OrderResponseDto;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.product.api.dto.ProductProgressResponseDto;
import hackathon.kb.chakchak.domain.product.domain.dto.ProductPreviewResponseDto;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.util.ProductToDtoMapper;
import java.util.Map;

public class OrderToDtoMapper {

    public static OrderResponseDto orderToOrderResponseDto(Order o, Map<Long, ProductProgressResponseDto> progressMap) {
        Product p = o.getProduct();

        // 이미지 N+1은 @BatchSize로 완화됨 (필요하면 썸네일만 또는 별도 API 권장)
        ProductPreviewResponseDto productPreview =
                ProductToDtoMapper.productToProductPreviewResponseDto(p, progressMap.get(p.getId()));

        return OrderResponseDto.builder()
                .orderId(o.getId())
                .quantity(o.getQuantity())
                .isSent(o.getIsSent())
                .deliveryCode(o.getDeliveryCode())
                .orderStatus(o.getStatus())
                .productPreview(productPreview)
                .build();
    }
}
