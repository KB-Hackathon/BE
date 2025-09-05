package hackathon.kb.chakchak.domain.member.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hackathon.kb.chakchak.domain.member.domain.dto.CouponInfo;
import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.domain.order.domain.entity.Coupon;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.order.repository.CouponRepository;
import hackathon.kb.chakchak.domain.order.repository.OrderRepository;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberCouponService {

	private final MemberRepository memberRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final CouponRepository couponRepository;

	/**
	 * 사용자가 보유한 쿠폰 목록 반환
	 * @param memberId
	 * @return
	 */
	@Transactional
	public List<CouponInfo> getMyCoupons(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ResponseCode.MEMBER_NOT_FOUND));

		List<Order> orders = orderRepository.findByMember(member);

		List<CouponInfo> couponInfos = new ArrayList<>();

		for (Order order : orders) {
			Product product = order.getProduct();
			Coupon coupon = order.getCoupon();

			CouponInfo couponInfo = new CouponInfo(coupon.getUuid(), coupon.getExpiration(), product.getTitle(),
				order.getQuantity());
			couponInfos.add(couponInfo);
		}

		return couponInfos;
	}

	@Transactional
	public void payCoupon(Long memberId, String code) {
		Coupon coupon = couponRepository.findByUuid(code)
			.orElseThrow(() -> new BusinessException(ResponseCode.COUPON_NOT_FOUND));

		coupon.useCoupon();
	}

}
