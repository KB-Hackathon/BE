package hackathon.kb.chakchak.domain.jwt.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieIssuer {

    public static final String REFRESH_TOKEN = "refresh_token";

    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshTtl;

    @Value("${auth.cookie.domain}")
    private String cookieDomain;

    @Value("${auth.cookie.secure}")
    private boolean cookieSecure;

    public ResponseCookie build(String token) {
        return ResponseCookie.from(REFRESH_TOKEN, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSecure ? "None" : "Lax") // 크로스 도메인 None, 로컬 Lax
//                .domain(cookieDomain)
                .path("/")
                .maxAge(refreshTtl)
                .build();
    }

    public ResponseCookie delete() {
        return ResponseCookie.from(REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSecure ? "None" : "Lax")
//                .domain(cookieDomain)
                .path("/")
                .maxAge(0)
                .build();
    }
}
