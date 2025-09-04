package hackathon.kb.chakchak.domain.auth.service;

import hackathon.kb.chakchak.domain.jwt.util.JwtIssuer;
import hackathon.kb.chakchak.domain.member.api.dto.req.AdditionalInfoRequest;
import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;
import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.domain.enums.MemberRole;
import hackathon.kb.chakchak.domain.member.domain.enums.SocialType;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.domain.auth.service.dto.SignupTokens;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtIssuer jwtIssuer;

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
}
