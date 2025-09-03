package hackathon.kb.chakchak.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.kb.chakchak.domain.jwt.util.JwtIssuer;
import hackathon.kb.chakchak.domain.jwt.util.CookieIssuer;
import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.global.security.CustomKakaoOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtIssuer jwtIssuer;
    private final CookieIssuer refreshCookieSupport;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("카카오 로그인에 성공했습니다.");

        CustomKakaoOAuth2User principal = (CustomKakaoOAuth2User) authentication.getPrincipal();
        Long kakaoId = principal.getKakaoId();
        log.info("[OAUTH2] success. kakaoId={}", kakaoId);

        invalidateSession(request, response); // 세션 정리

        Optional<Member> member = memberRepository.findByKakaoId(kakaoId);
        log.info("우리의 DB에서 해당 사용자가 존재하는지 확인힙니다.: {}", member);

        if (member.isPresent()) {
            Member m = member.get();

            String access = jwtIssuer.createAccessToken(m.getId(), m.getRole().name());
            String refresh = jwtIssuer.createRefreshToken(m.getId());

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookieSupport.build(refresh).toString());

            objectMapper.writeValue(response.getWriter(), Map.of(
                    "registered", true,
                    "accessToken", access
            ));
            return;
        }

        // 신규 회원 — 추가 필수 항목을 우리 폼에서 받아야 함
        String signupToken = jwtIssuer.createSignupToken(kakaoId);
        log.info("[SIGNUP] need more info(name, age, address). kakaoId={}", kakaoId);

        objectMapper.writeValue(response.getWriter(), Map.of(
                "registered", false,
                "signupToken", signupToken
        ));
    }

    private void invalidateSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        response.addHeader(HttpHeaders.SET_COOKIE, "JSESSIONID=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
    }

}

