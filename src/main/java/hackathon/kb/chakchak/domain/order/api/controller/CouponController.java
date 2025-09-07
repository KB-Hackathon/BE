package hackathon.kb.chakchak.domain.order.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.order.api.dto.res.MyCouponListResponse;
import hackathon.kb.chakchak.domain.order.api.dto.res.CouponItemDto;
import hackathon.kb.chakchak.domain.order.service.CouponService;
import hackathon.kb.chakchak.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "사용자의 쿠폰 목록", description = "구매자의 보유 쿠폰을 유효기간 순으로 조회합니다.")
    @GetMapping("/me")
    public BaseResponse<MyCouponListResponse> getMyCouponList(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        return BaseResponse.OK(couponService.getMyCouponList(principal.getId()));
    }

    @Operation(summary = "쿠폰 1개 정보", description = "쿠폰 1개의 정보를 조회합니다.")
    @GetMapping("/{couponId}")
    public BaseResponse<CouponItemDto> getCouponInfo(@PathVariable Long couponId) {
        return BaseResponse.OK(couponService.getCouponInfo(couponId));
    }


    @Operation(summary = "쿠폰 uuid로 쿠폰 조회", description = "쿠폰 UUID로 단일 쿠폰 정보를 조회합니다.")
    @GetMapping("")
    public BaseResponse<CouponItemDto> getBuyerCouponByUUID(@RequestParam String uuid) {
        return BaseResponse.OK(couponService.getBuyerCouponByUUID(uuid));
    }
}