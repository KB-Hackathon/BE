package hackathon.kb.chakchak.domain.oauth.api.controller;

import hackathon.kb.chakchak.domain.jwt.util.CookieIssuer;
import hackathon.kb.chakchak.domain.member.api.dto.req.AdditionalInfoRequest;
import hackathon.kb.chakchak.domain.oauth.service.OauthService;
import hackathon.kb.chakchak.domain.oauth.service.dto.SignupTokens;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
@Slf4j
public class OauthController {

    private final CookieIssuer refreshCookieSupport;
    private final OauthService oauthService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshTtl;

    @Value("${auth.cookie.domain:}")
    private String cookieDomain;

    @Value("${auth.cookie.secure:true}")
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
}

