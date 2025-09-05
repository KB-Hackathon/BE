package hackathon.kb.chakchak.domain.auth.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.jwt.util.CookieIssuer;
import hackathon.kb.chakchak.domain.member.api.dto.req.AdditionalInfoRequest;
import hackathon.kb.chakchak.domain.auth.service.AuthService;
import hackathon.kb.chakchak.domain.auth.service.dto.SignupTokens;
import hackathon.kb.chakchak.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
@Tag(name = "회원 관리 API", description = "회원 관리 API")
public class OauthController {

    private final CookieIssuer refreshCookieSupport;
    private final AuthService oauthService;
    private final AuthService authService;
    private final CookieIssuer cookieIssuer;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshTtl;

    @Value("${auth.cookie.domain}")
    private String cookieDomain;

    @Value("${auth.cookie.secure}")
    private boolean cookieSecure;

    @Operation(summary = "신규 사용자 추가 정보 받기", description = "사용자로부터 추가 정보를 받아 저장합니다.")
    @PostMapping("/signup/additional")
    public BaseResponse<AdditionalInfoResponse> complete(@Valid @RequestBody AdditionalInfoRequest req,
                                                         HttpServletResponse res) {
        SignupTokens tokens = oauthService.completeSignup(req);

        res.addHeader(HttpHeaders.SET_COOKIE, refreshCookieSupport.build(tokens.getRefreshToken()).toString());

        return BaseResponse.OK(new AdditionalInfoResponse(tokens.getAccessToken()));

    }

    @Operation(summary = "서비스 탈퇴", description = "서비스 탈퇴를 진행합니다.")
    @DeleteMapping("/member")
    public BaseResponse<Void> withdraw(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.withdraw(principal, authorization, request);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieIssuer.delete().toString());

        return BaseResponse.OK();
    }

    public record AdditionalInfoResponse(String accessToken) {}

}

