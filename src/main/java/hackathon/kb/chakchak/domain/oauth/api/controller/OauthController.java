package hackathon.kb.chakchak.domain.oauth.api.controller;

import hackathon.kb.chakchak.domain.jwt.util.CookieIssuer;
import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;
import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.domain.enums.MemberRole;
import hackathon.kb.chakchak.domain.member.domain.enums.SocialType;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.domain.member.api.dto.req.AdditionalInfoRequest;
import hackathon.kb.chakchak.domain.jwt.util.JwtIssuer;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
public class OauthController {

    private final MemberRepository memberRepository;
    private final JwtIssuer jwtIssuer;
    private final CookieIssuer refreshCookieSupport;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshTtl;

    @Value("${auth.cookie.domain:}")
    private String cookieDomain;

    @Value("${auth.cookie.secure:true}")
    private boolean cookieSecure;

    @PostMapping("/signup/complete")
    public ResponseEntity<?> complete(@Valid @RequestBody AdditionalInfoRequest req) {

        // signupToken 검증
        Claims claims = jwtIssuer.parseJws(req.getSignupToken()).getBody();

        if (!"signup".equals(claims.get("typ"))) {
//            return new BusinessException(ResponseCode.SIGNUP_TOKEN_INVALID);
            return ResponseEntity.badRequest().body(Map.of("msg", "INVALID_SIGNUP_TOKEN"));
        }

        Long kakaoId = ((Number) claims.get("kakaoId")).longValue();

        // 이미 가입된 사용자 → 추가 회원가입 불필요(409)
        if (memberRepository.existsByKakaoId(kakaoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ALREADY_REGISTERED");
//            return new BusinessException(ResponseCode.SIGNUP_NOT_REQUIRED);
        }

        Member saved = memberRepository.save(
                Buyer.builder()
                        .name(req.getName())
                        .age(req.getAge())
                        .address(req.getAddress())
                        .phoneNumber(req.getPhoneNumber())
                        .social(SocialType.KAKAO)
                        .kakaoId(kakaoId)
                        .role(MemberRole.BUYER)
                        .image(req.getImage())
                        .build()
        );

        String access = jwtIssuer.createAccessToken(saved.getId(), saved.getRole().name());
        String refresh = jwtIssuer.createRefreshToken(saved.getId());

        String cookie = refreshCookieSupport.build(refresh).toString();

        log.info("[SIGNUP] completed. memberId={}", saved.getId());

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie)
                .body(Map.of(
                        "registered", true,
                        "accessToken", access
                ));
    }
}

