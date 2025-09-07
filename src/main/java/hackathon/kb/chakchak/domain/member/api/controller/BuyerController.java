package hackathon.kb.chakchak.domain.member.api.controller;

import hackathon.kb.chakchak.domain.member.api.dto.res.BuyerOrderListResponse;
import hackathon.kb.chakchak.domain.member.api.dto.res.MemberProfileResponse;
import hackathon.kb.chakchak.domain.member.service.BuyerService;
import hackathon.kb.chakchak.domain.member.service.MemberService;
import hackathon.kb.chakchak.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/buyers")
@RequiredArgsConstructor
@Tag(name = "BUYER API", description = "구매자 관련 API")
public class BuyerController {

    private final MemberService memberService;
    private final BuyerService buyerService;

    @Operation(summary = "구매자 기본 정보 조회", description = "구매자 아이디를 기반으로 구매자 기본 정보를 조회합니다.")
    @GetMapping("/{buyerId}")
    public BaseResponse<MemberProfileResponse> getBuyerProfile(@PathVariable(name = "buyerId") Long buyerId) {
        return BaseResponse.OK(memberService.getBuyerProfile(buyerId));
    }

    @Operation(summary = "구매자 공구 조회", description = "구매자 아이디를 기반으로 공동구매 리스트(최신순)를 조회합니다.")
    @GetMapping("/orders/{buyerId}")
    public BaseResponse<BuyerOrderListResponse> getOrderList(@PathVariable(name = "buyerId") Long buyerId) {
        return BaseResponse.OK(buyerService.getOrderList(buyerId));
    }
}
