package hackathon.kb.chakchak.global.oauth.kakao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
public class KakaoApiClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${kakao.admin-key}")
    private String adminKey;

    public void unlinkByAdminKey(Long kakaoId) {
        WebClient webClient = webClientBuilder
                .baseUrl("https://kapi.kakao.com")
                .build();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("target_id_type", "user_id");
        form.add("target_id", String.valueOf(kakaoId));

        webClient.post()
                .uri("/v1/user/unlink")
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
