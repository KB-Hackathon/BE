package hackathon.kb.chakchak.domain.member.service;

import hackathon.kb.chakchak.domain.member.api.dto.res.BuyerOrderListResponse;
import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.order.domain.dto.OrderResponseDto;
import hackathon.kb.chakchak.domain.order.repository.OrderRepository;
import hackathon.kb.chakchak.domain.product.api.dto.ProductProgressResponseDto;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import hackathon.kb.chakchak.domain.product.util.ProductToDtoMapper;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuyerService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;


    public BuyerOrderListResponse getOrderList(Long buyerId) {
        Member buyer = memberRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ResponseCode.BUYER_NOT_FOUND));

        // 전체 주문 + 상품 로딩
        List<Order> orders = orderRepository.findOrdersWithProductByBuyer(buyerId);

        // DTO 매핑
        List<OrderResponseDto> items = orders.stream().map(o -> {
            Product p = o.getProduct();
            // 상품에 대한 집계값
            ProductProgressResponseDto progress = productRepository.findProgressByProductId(p.getId());
            return OrderResponseDto.builder()
                    .orderId(o.getId())
                    .quantity(o.getQuantity())
                    .isSent(o.getIsSent())
                    .deliveryCode(o.getDeliveryCode())
                    .orderStatus(o.getStatus())
                    .productPreview(ProductToDtoMapper.productToProductPreviewResponseDto(p, progress))
                    .build();
        }).toList();

        return BuyerOrderListResponse.builder()
                .orders(items)
                .build();
    }
}
