package hackathon.kb.chakchak.domain.member.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.member.domain.dto.CouponInfo;
import hackathon.kb.chakchak.domain.member.service.MemberCouponService;
import hackathon.kb.chakchak.global.response.BaseResponse;
import hackathon.kb.chakchak.global.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RequestMapping("/auth")
@RequiredArgsConstructor
public class CouponController {

	private final MemberCouponService memberCouponService;

	/**
	 * 사용자가 보유한 쿠폰 정보 조회
	 * @param memberPrincipal
	 * @return
	 */
	@GetMapping("/member/my/coupon")
	@Operation(summary = "사용자 소유 쿠폰 조회", description = "사용자가 구매한 쿠폰의 정보를 반환")
	public ResponseEntity<BaseResponse<List<CouponInfo>>> getCoupons(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
		List<CouponInfo> res = memberCouponService.getMyCoupons(memberPrincipal.getId());

		ResponseCode responseCode = ResponseCode.SUCCESS;

		return ResponseEntity.ok(BaseResponse.OK(res));
	}

	/**
	 * 판매자의 쿠폰 사용 처리 api
	 * @param memberPrincipal
	 * @param code
	 * @return
	 */
	@PostMapping("/seller/manage/pay/coupon")
	@Operation(summary = "판매자 쿠폰 결제", description = "구매자의 쿠폰 코드를 받아 사용처리합니다.")
	public ResponseEntity<BaseResponse<List<?>>> getCoupons(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal, @RequestBody String code) {

		memberCouponService.payCoupon(memberPrincipal.getId(), code);
		return ResponseEntity.ok(BaseResponse.OK());
	}
}
