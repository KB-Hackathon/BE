package hackathon.kb.chakchak.domain.order.api.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.order.api.dto.req.CouponOrderReq;
import hackathon.kb.chakchak.domain.order.api.dto.res.CouponOrderRes;
import hackathon.kb.chakchak.domain.order.service.OrderService;
import hackathon.kb.chakchak.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/seller/order")
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/coupon")
	public BaseResponse<CouponOrderRes> orderCoupon(@AuthenticationPrincipal MemberPrincipal principal,
		CouponOrderReq couponOrderReq) {
		CouponOrderRes res = orderService.orderCoupon(principal.getId(), couponOrderReq);

		return BaseResponse.OK(res);
	}

}