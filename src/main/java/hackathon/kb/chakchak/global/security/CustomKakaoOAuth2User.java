package hackathon.kb.chakchak.global.security;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomKakaoOAuth2User implements OAuth2User {

    private final Collection<? extends GrantedAuthority> authorities; // 권한
    private final Map<String, Object> attributes; // 카카오로부터 받은 정보
    private final String nameAttributeKey; // "kakaoId"

    public CustomKakaoOAuth2User(Collection<? extends GrantedAuthority> authorities,
                                 Map<String, Object> attributes,
                                 String nameAttributeKey) {
        this.authorities = authorities;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.getOrDefault(nameAttributeKey, ""));
    }

    public Long getKakaoId() {
        Object v = attributes.get("kakaoId");
        return (v instanceof Number) ? ((Number) v).longValue() : Long.valueOf(String.valueOf(v));
    }

    public String getNickname() {
        return (String) attributes.get("nickname");
    }

    public String getProfileImageUrl() {
        return (String) attributes.get("profileImageUrl");
    }
}
