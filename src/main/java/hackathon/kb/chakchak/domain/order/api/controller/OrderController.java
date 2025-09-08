package hackathon.kb.chakchak.domain.order.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.order.api.dto.req.CouponOrderReq;
import hackathon.kb.chakchak.domain.order.api.dto.res.CouponOrderRes;
import hackathon.kb.chakchak.domain.order.service.OrderService;
import hackathon.kb.chakchak.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "ORDER API", description = "상품 주문 관련 API")
@RequestMapping("/auth")
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/buyer/order/coupon")
	@Operation(summary = "쿠폰 구매 요청", description = "구매자가 쿠폰을 구매할 수 있습니다. 쿠폰은 자동 발급됩니다.")
	public ResponseEntity<BaseResponse<CouponOrderRes>> orderCoupon(@AuthenticationPrincipal MemberPrincipal principal,
		@RequestBody CouponOrderReq couponOrderReq) {
		CouponOrderRes res = orderService.orderCoupon(principal.getId(), couponOrderReq);

		return ResponseEntity.ok(BaseResponse.OK(res));
	}

	@PostMapping("/seller/order/coupon/terminate")
	@Operation(summary = "쿠폰 공동 구매 종료", description = "판매자가 쿠폰 공동 구매를 종료하고 공구에 참여한 사용자들에게 쿠폰이 발급됩니다.")
	public ResponseEntity<BaseResponse<?>> end(@AuthenticationPrincipal MemberPrincipal principal,
		@RequestBody Long productId) {
		orderService.terminateCoupon(productId);

		return ResponseEntity.ok(BaseResponse.OK());
	}

}