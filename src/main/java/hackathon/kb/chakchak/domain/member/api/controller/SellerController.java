package hackathon.kb.chakchak.domain.member.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.member.api.dto.req.BizRegisterRequest;
import hackathon.kb.chakchak.domain.member.api.dto.res.BizRegisterResponse;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.member.service.SellerService;
import hackathon.kb.chakchak.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
@Tag(name = "사업자 등록 API", description = "에이픽 API와 주소 API 호출 후 사업자 등록 번호 확인 및 행정동 코드 추출 API")
public class SellerController {
    private final SellerService sellerService;

    @PostMapping("/register")
    public BaseResponse<BizRegisterResponse> register(
            @Valid @RequestBody BizRegisterRequest req,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Seller saved = sellerService.updateSellerFromApick(req.getBizNo(), principal.getId());
        return BaseResponse.OK(BizRegisterResponse.from(saved));
    }
}
