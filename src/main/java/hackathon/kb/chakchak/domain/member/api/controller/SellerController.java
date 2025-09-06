package hackathon.kb.chakchak.domain.member.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.member.api.dto.req.BizRegisterRequest;
import hackathon.kb.chakchak.domain.member.api.dto.res.BizRegisterResponse;
import hackathon.kb.chakchak.domain.member.api.dto.res.SellerReadResponseDto;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.member.service.SellerBasicService;
import hackathon.kb.chakchak.domain.member.service.SellerService;
import hackathon.kb.chakchak.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
@Tag(name = "SELLER API", description = "판매자(사업자) 관련 API")
public class SellerController {
    private final SellerService sellerService;
    private final SellerBasicService sellerBasicService;

    @Operation(summary = "사업자 등록", description = "사업자 등록번호를 토대로 관련된 사업자 정보를 반환합니다.")
    @PostMapping("/register")
    public BaseResponse<BizRegisterResponse> register(
            @Valid @RequestBody BizRegisterRequest req,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Seller saved = sellerService.updateSellerFromApick(req.getBizNo(), principal.getId());
        return BaseResponse.OK(BizRegisterResponse.from(saved));
    }

    @Operation(summary = "판매자 조회", description = "판매자 아이디를 기반으로, 판매자의 정보와 관련된 상품 글을 조회합니다.")
    @GetMapping("/{sellerId}")
    public BaseResponse<SellerReadResponseDto> listBySellerId(
            @PathVariable(name = "sellerId") Long sellerId
    ){
        return BaseResponse.OK(sellerBasicService.getSellerById(sellerId));
    }
}
