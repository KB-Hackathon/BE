package hackathon.kb.chakchak.domain.auth.service;

import hackathon.kb.chakchak.domain.jwt.util.CookieIssuer;
import hackathon.kb.chakchak.domain.jwt.util.JwtIssuer;
import hackathon.kb.chakchak.domain.member.api.dto.req.AdditionalInfoRequest;
import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;
import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.domain.enums.MemberRole;
import hackathon.kb.chakchak.domain.member.domain.enums.SocialType;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.domain.auth.service.dto.SignupTokens;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.redis.util.RedisUtil;
import hackathon.kb.chakchak.global.response.ResponseCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtIssuer jwtIssuer;
    private final RedisUtil redisUtil;
    private final CookieIssuer cookieIssuer;

    @Transactional
    public SignupTokens completeSignup(AdditionalInfoRequest req) {
        // signupToken 검증
        Claims claims = jwtIssuer.parseJws(req.getSignupToken()).getBody();
        String typ = claims.get("typ", String.class);

        if (!"signup".equals(typ)) {
            throw new BusinessException(ResponseCode.SIGNUP_TOKEN_INVALID);
        }

        Long kakaoId = ((Number) claims.get("kakaoId")).longValue();
        log.info("가입을 요청하는 카카오 아이디입니다: {}", kakaoId);

        // 이미 가입된 사용자 방지
        if (memberRepository.existsByKakaoId(kakaoId)) {
            throw new BusinessException(ResponseCode.SIGNUP_NOT_REQUIRED);
        }

        // 신규 사용자 저장
        Member saved = memberRepository.save(
                Buyer.builder()
                        .name(req.getName())
                        .age(req.getAge())
                        .gender(req.getGender())
                        .address(req.getAddress())
                        .phoneNumber(req.getPhoneNumber())
                        .social(SocialType.KAKAO)
                        .kakaoId(kakaoId)
                        .role(MemberRole.BUYER)
                        .image(req.getImage())
                        .build()
        );

        log.info("[SIGNUP] completed. memberId={}", saved.getId());

        // 토큰 발급
        String access = jwtIssuer.createAccessToken(saved.getId(), saved.getRole().name());
        String refresh = jwtIssuer.createRefreshToken(saved.getId());

        return new SignupTokens(true, access, refresh);
    }

    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refresh = extractRefreshCookie(request);

        if (refresh == null || refresh.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("code", "NO_REFRESH_COOKIE"));
        }

        try {
            Claims claims = jwtIssuer.parseJws(refresh).getBody();
            String typ = claims.get("typ", String.class);
            if (!"refresh".equals(typ)) {
                deleteRefreshTokenFromCookie(response);
                return ResponseEntity.status(401).body(Map.of("msg", "NOT_REFRESH_TOKEN"));
            }

            Long memberId = Long.valueOf(claims.getSubject());

            // Redis에 저장된 최신 리프레시와 일치하는지 확인
            String savedRefreshToken = redisUtil.get("refresh:" + memberId);

            if (savedRefreshToken == null || !savedRefreshToken.equals(refresh)) {
                deleteRefreshTokenFromCookie(response);
                return ResponseEntity.status(401).body(Map.of("msg", "유효하지 않은 refresh 토큰"));
            }

            // 회전: 기존 저장본 삭제
            redisUtil.delete("refresh:" + memberId);

            Member member = memberRepository.findById(memberId).orElse(null);
            if (member == null) {
                deleteRefreshTokenFromCookie(response);
                return ResponseEntity.status(401).body(Map.of("msg", "USER_NOT_FOUND"));
            }

            // 새 토큰 발급
            String newAccess  = jwtIssuer.createAccessToken(member.getId(), member.getRole().name());
            String newRefresh = jwtIssuer.createRefreshToken(member.getId());

            // 새 리프레시 저장
            redisUtil.set("refresh:" + member.getId(), newRefresh);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookieIssuer.build(newRefresh).toString())
                    .body(Map.of("accessToken", newAccess));

        } catch (ExpiredJwtException e) {
            log.debug("REFRESH expired", e);
            // 만료: Redis에서 해당 사용자 refresh 제거 + 쿠키 삭제 → 401
            try {
                Claims expired = e.getClaims();
                if (expired != null && expired.getSubject() != null) {
                    Long memberId = Long.valueOf(expired.getSubject());
                    redisUtil.delete("refresh:" + memberId);
                }
            } catch (Exception ignore) { }
            deleteRefreshTokenFromCookie(response);
            return ResponseEntity.status(401).body(Map.of("code", "REFRESH_EXPIRED"));

        } catch (JwtException e) {
            log.debug("REFRESH invalid", e);
            deleteRefreshTokenFromCookie(response);
            return ResponseEntity.status(401).body(Map.of("code", "REFRESH_INVALID"));
        } catch (IllegalArgumentException e) {
            log.debug("REFRESH illegal arg", e);
            deleteRefreshTokenFromCookie(response);
            return ResponseEntity.status(401).body(Map.of("code", "REFRESH_ILLEGAL_ARGUMENT"));
        }
    }

    private String extractRefreshCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (CookieIssuer.REFRESH_TOKEN.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private void deleteRefreshTokenFromCookie(HttpServletResponse response) {
        try {
            ResponseCookie deleteCookie = cookieIssuer.delete(); // Max-Age=0 등
            response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        } catch (Exception ex) {
            log.warn("failed to delete refresh cookie", ex);
        }
    }
}
