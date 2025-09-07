package hackathon.kb.chakchak.domain.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokensResponse {
    private final String accessToken;
    private final String refreshToken;
}
