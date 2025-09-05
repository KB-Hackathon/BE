package hackathon.kb.chakchak.global.oauth.kakao.service;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // 카카오에서 사용자 정보 가져옴

        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("kakao에서 로그인하고 받아온 정보입니다. {}", attributes.toString());

        Long kakaoId = ((Number) attributes.get("id")).longValue();

        Map<String, Object> mapped = new HashMap<>();
        mapped.put("kakaoId", kakaoId);

        Collection<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
        return new CustomKakaoOAuth2User(authorities, mapped, "kakaoId");
    }
}

