package hackathon.kb.chakchak.domain.member.service;

import hackathon.kb.chakchak.domain.member.api.dto.res.BuyerOrderListResponse;
import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;
import hackathon.kb.chakchak.domain.member.repository.BuyerRepository;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.order.domain.dto.OrderResponseDto;
import hackathon.kb.chakchak.domain.order.repository.OrderRepository;
import hackathon.kb.chakchak.domain.product.api.dto.ProductProgressResponseDto;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import hackathon.kb.chakchak.domain.product.util.ProductToDtoMapper;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import java.util.ArrayList;
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
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public BuyerOrderListResponse getOrderList(Long buyerId) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ResponseCode.BUYER_NOT_FOUND));

        // SUCCESS, PENDING만
        List<Order> orders = orderRepository.findOrdersWithProductByBuyer(buyerId);

        if (orders.isEmpty()) {
            return BuyerOrderListResponse.builder()
                    .orders(List.of())
                    .successOrders(List.of())
                    .pendingOrders(List.of())
                    .build();
        }

        // 뽑은 order 리스트 중에서 해당되는 상품ID list
        List<Long> productIds = orders.stream()
                .map(o -> o.getProduct().getId())
                .distinct()
                .toList();

        Map<Long, ProductProgressResponseDto> progressMap =
                productRepository.findProgressByProductIds(productIds).stream()
                        .map(v -> new ProductProgressResponseDto(v.getId(), v.getOrderCount(), v.getPercentAchieved()))
                        .collect(Collectors.toMap(ProductProgressResponseDto::id, Function.identity()));

        // 상태별 분류
        List<OrderResponseDto> orderList = new ArrayList<>();
        List<OrderResponseDto> successOrderList = new ArrayList<>();
        List<OrderResponseDto> pendingOrderList = new ArrayList<>();

        for (Order o : orders) {
            Product p = o.getProduct();
            ProductProgressResponseDto progress =
                    progressMap.getOrDefault(p.getId(), new ProductProgressResponseDto(p.getId(), 0L, 0));

            OrderResponseDto dto = OrderResponseDto.builder()
                    .orderId(o.getId())
                    .quantity(o.getQuantity())
                    .isSent(o.getIsSent())
                    .deliveryCode(o.getDeliveryCode())
                    .productPreview(ProductToDtoMapper.productToProductPreviewResponseDto(p, progress))
                    .build();

            if (p.getStatus() == ProductStatus.SUCCESS) {
                successOrderList.add(dto);
            } else if (p.getStatus() == ProductStatus.PENDING) {
                pendingOrderList.add(dto);
            }
            orderList.add(dto);
        }

        return BuyerOrderListResponse.builder()
                .orders(orderList)
                .successOrders(successOrderList)
                .pendingOrders(pendingOrderList)
                .build();
    }
}
