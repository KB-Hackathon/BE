package hackathon.kb.chakchak.domain.member.api.controller;

import hackathon.kb.chakchak.domain.jwt.util.JwtIssuer;
import hackathon.kb.chakchak.domain.jwt.util.CookieIssuer;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtIssuer jwtIssuer;
    private final CookieIssuer refreshCookieSupport;

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
//        String refresh = extractRefreshCookie(request);
//        log.info("refresh: {}", refresh);
//
//        if (refresh == null) {
//            return ResponseEntity.status(401).body(Map.of("msg", "NO_REFRESH_COOKIE"));
//        }
//
////        try {
//            Claims claims = jwtIssuer.parseJws(refresh).getBody();
//            String typ = claims.get("typ", String.class);
//            if (!"refresh".equals(typ)) {
//                return ResponseEntity.status(401).body(Map.of("msg", "NOT_REFRESH_TOKEN"));
//            }
//
//            Long memberId = Long.valueOf(claims.getSubject());
//            Member member = memberRepository.findById(memberId)
//                    .orElse(null);
//            if (member == null) {
//                return ResponseEntity.status(401).body(Map.of("msg", "USER_NOT_FOUND"));
//            }
//
//            String newAccess = jwtIssuer.createAccessToken(member.getId(), member.getRole().name());
//            String newRefresh = jwtIssuer.createRefreshToken(member.getId()); // 회전
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.SET_COOKIE, refreshCookieSupport.build(newRefresh).toString())
//                    .body(Map.of("accessToken", newAccess));

//        } catch (JwtException e) {
//            return ResponseEntity.status(401).body(Map.of("msg", "INVALID_REFRESH_TOKEN"));
//        }

        // 필터가 심어둔 결과 읽기
        Object sub = request.getAttribute("JWT_SUBJECT");
        Object role = request.getAttribute("JWT_ROLE");
        Object err  = request.getAttribute("JWT_REFRESH_ERROR_CODE");

        if (err != null || sub == null) {
            // 만료/위조/타입오류 등
            return ResponseEntity.status(401).body(Map.of(
                    "code", "REFRESH_INVALID",
                    "detail", String.valueOf(request.getAttribute("JWT_REFRESH_ERROR_MSG"))
            ));
        }

        Long memberId = Long.valueOf(String.valueOf(sub));
        String r = (role == null ? "BUYER" : String.valueOf(role)); // 저장 안 했다면 DB 조회해도 됨

        // 새 토큰 발급 + 쿠키 회전
        String access  = jwtIssuer.createAccessToken(memberId, r);
        String refresh = jwtIssuer.createRefreshToken(memberId);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookieSupport.build(refresh).toString())
                .body(Map.of("accessToken", access));
    }


}