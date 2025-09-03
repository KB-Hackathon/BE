package hackathon.kb.chakchak.domain.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupTokens {
    private final boolean registered;   // 항상 true (완료 후)
    private final String accessToken;
    private final String refreshToken;
}
