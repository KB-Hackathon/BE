package hackathon.kb.chakchak.domain.auth.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.jwt.util.CookieIssuer;
import hackathon.kb.chakchak.domain.member.api.dto.req.AdditionalInfoRequest;
import hackathon.kb.chakchak.domain.auth.service.AuthService;
import hackathon.kb.chakchak.domain.auth.service.dto.SignupTokens;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshTtl;

    @Value("${auth.cookie.domain}")
    private String cookieDomain;

    @Value("${auth.cookie.secure}")
    private boolean cookieSecure;

    @PostMapping("/signup/additional")
    public ResponseEntity<?> complete(@Valid @RequestBody AdditionalInfoRequest req) {
        SignupTokens tokens = oauthService.completeSignup(req);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookieSupport.build(tokens.getRefreshToken()).toString())
                .body(Map.of(
                        "registered", tokens.isRegistered(),
                        "accessToken", tokens.getAccessToken()
                ));
    }

    @Operation(summary = "서비스 탈퇴", description = "서비스 탈퇴를 진행합니다.")
    @DeleteMapping("/member")
    public ResponseEntity<?> withdraw(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            HttpServletRequest request
    ) {
        return authService.withdraw(principal, authorization, request);
    }

}

