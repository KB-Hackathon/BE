package hackathon.kb.chakchak.domain.order.service;

import hackathon.kb.chakchak.domain.member.domain.dto.CouponInfo;
import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;
import hackathon.kb.chakchak.domain.member.repository.BuyerRepository;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.domain.order.api.dto.req.CouponOrderReq;
import hackathon.kb.chakchak.domain.order.api.dto.res.CouponItemDto;
import hackathon.kb.chakchak.domain.order.api.dto.res.MyCouponListResponse;
import hackathon.kb.chakchak.domain.order.api.dto.res.CouponOrderRes;
import hackathon.kb.chakchak.domain.order.domain.entity.Coupon;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.order.repository.CouponRepository;
import hackathon.kb.chakchak.domain.order.repository.OrderRepository;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import io.swagger.v3.core.jackson.mixin.MediaTypeMixin;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final MemberRepository memberRepository;
    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;

    /**
     *
     * @param memberId
     * @param couponOrderReq : 구매할 쿠푠 개수
     * @return
     */
    @Transactional
    public CouponOrderRes orderCoupon(Long memberId, CouponOrderReq couponOrderReq) {
        Buyer buyer = buyerRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));

        Product product = productRepository.findById(couponOrderReq.getProductId())
                .orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));

        Order order = Order.builder()
                .product(product)
                .buyer(buyer)
                .quantity(couponOrderReq.getQuantity())
                .build();

        Coupon coupon = Coupon.builder()
                .uuid(UUID.randomUUID().toString().replace("-", ""))
                .expiration(LocalDate.now().plusYears(1L))
                .build();

        orderRepository.save(order);
        couponRepository.save(coupon);

        return new CouponOrderRes(coupon.getUuid(), coupon.getExpiration());
    }

    @Transactional(readOnly = true)
    public MyCouponListResponse getMyCouponList(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }

        List<CouponItemDto> coupons = couponRepository.findMyCoupons(memberId);
        return MyCouponListResponse.builder()
                .coupons(coupons)
                .build();
    }

    @Transactional(readOnly = true)
    public CouponItemDto getCouponInfo(Long couponId) {
        if (!couponRepository.existsById(couponId)) {
            throw new BusinessException(ResponseCode.COUPON_NOT_FOUND);
        }

        return couponRepository.findCoupon(couponId);
    }

    @Transactional(readOnly = true)
    public CouponItemDto getBuyerCoupon(Long buyerId, Long couponId) {
        if (!buyerRepository.existsById(buyerId)) {
            throw new BusinessException(ResponseCode.BUYER_NOT_FOUND);
        }
        return couponRepository.findBuyerCouponById(buyerId, couponId)
                .orElseThrow(() -> new BusinessException(ResponseCode.COUPON_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public CouponItemDto getBuyerCouponByUUID(String uuid) {

        return couponRepository.findBuyerCouponByUUID(uuid)
                .orElseThrow(() -> new BusinessException(ResponseCode.COUPON_NOT_FOUND));
    }

}
