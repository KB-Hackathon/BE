package hackathon.kb.chakchak.domain.member.service;

import hackathon.kb.chakchak.domain.member.api.dto.res.BuyerOrderListResponse;
import hackathon.kb.chakchak.domain.member.api.dto.res.MemberProfileResponse;
import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.member.repository.BuyerRepository;
import hackathon.kb.chakchak.domain.order.domain.dto.OrderResponseDto;
import hackathon.kb.chakchak.domain.order.repository.OrderRepository;
import hackathon.kb.chakchak.domain.order.util.OrderToDtoMapper;
import hackathon.kb.chakchak.domain.product.api.dto.ProductProgressResponseDto;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import hackathon.kb.chakchak.domain.product.util.ProductToDtoMapper;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuyerService {
    private final BuyerRepository buyerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public MemberProfileResponse getBuyerProfile(Long buyerId) {
        Buyer b = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ResponseCode.BUYER_NOT_FOUND));

        return MemberProfileResponse.from(b);
    }


    public BuyerOrderListResponse getOrderList(Long buyerId) {

        Buyer buyer = buyerRepository.findByIdWithOrdersAndProduct(buyerId)
                .orElseThrow(() -> new BusinessException(ResponseCode.BUYER_NOT_FOUND));

        log.info("buyer id={}, name={}", buyer.getId(), buyer.getName());

        // 구매자가 구매한 상품의 ID 수집
        List<Long> productIdList = buyer.getOrders().stream()
                .map(o -> o.getProduct().getId())
                .distinct()
                .toList();

        log.info("구매한 상품의 id 리스트: {}", productIdList);

        if (productIdList.isEmpty()) {
            return BuyerOrderListResponse.builder()
                    .orders(List.of())
                    .build();
        }

        // 진행률/집계 일괄 조회
        Map<Long, ProductProgressResponseDto> progressMap =
                productRepository.findProgressDtoByProductIds(productIdList).stream()
                        .collect(Collectors.toMap(ProductProgressResponseDto::id, Function.identity()));

        // 매핑 시 progressMap 사용 (N+1 방지)
        List<OrderResponseDto> items = buyer.getOrders().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .map(o -> OrderToDtoMapper.orderToOrderResponseDto(o, progressMap))
                .toList();

        return BuyerOrderListResponse.builder()
                .orders(items)
                .build();
    }

    public BuyerOrderListResponse getBuyerOrders(Long buyerId) {
        Buyer buyer = buyerRepository.findByIdWithOrdersAndProduct(buyerId)
                .orElseThrow(() -> new BusinessException(ResponseCode.BUYER_NOT_FOUND));

        // 1) 전체 주문 + 상품 로딩 (fetch join)
        List<Order> orders = orderRepository.findAllWithProductByBuyer(buyerId);

        // 3) DTO 매핑 (집계값 주입)
        List<OrderResponseDto> items = orders.stream().map(o -> {
            var p = o.getProduct();

            ProductProgressResponseDto present = productRepository.findProgressByProductId(p.getId());
            return OrderResponseDto.builder()
                    .orderId(o.getId())
                    .quantity(o.getQuantity())
                    .isSent(o.getIsSent())
                    .deliveryCode(o.getDeliveryCode())
                    .orderStatus(o.getStatus())
                    .productPreview(ProductToDtoMapper.productToProductPreviewResponseDto(p, present))
                    .build();
        }).toList();

        return BuyerOrderListResponse.builder()
                .orders(items)
                .build();
    }


}
