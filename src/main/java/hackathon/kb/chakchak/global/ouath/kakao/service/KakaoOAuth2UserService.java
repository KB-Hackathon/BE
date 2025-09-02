package hackathon.kb.chakchak.global.ouath.kakao.service;

import hackathon.kb.chakchak.global.security.CustomKakaoOAuth2User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
//@Transactional
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // 카카오에서 사용자 정보 가져옴

        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.debug(attributes.toString());

        Long kakaoId = ((Number) attributes.get("id")).longValue();

        String nickname = null; // 필수
        String profileImageUrl = null; // 선택

        // TODO: 다듬자
        Object kakaoAccountObj = attributes.get("kakao_account");
        if (kakaoAccountObj instanceof Map<?, ?> kakaoAccount) {
            Object profileObj = kakaoAccount.get("profile");
            if (profileObj instanceof Map<?, ?> profile) {
                nickname = asString(profile.get("nickname")); // 필수 동의
                profileImageUrl = asString(profile.get("profile_image_url")); // 선택 동의
            }
        }

        log.info("[KAKAO] user loaded. id={}, nickname={}, profileImage={}", kakaoId, nickname, profileImageUrl);

        Map<String, Object> mapped = new HashMap<>();
        mapped.put("kakaoId", kakaoId);
        if (nickname != null) mapped.put("nickname", nickname);
        if (profileImageUrl != null) mapped.put("profileImageUrl", profileImageUrl);

        Collection<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
        return new CustomKakaoOAuth2User(authorities, mapped, "kakaoId");
    }

    private String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }
}

