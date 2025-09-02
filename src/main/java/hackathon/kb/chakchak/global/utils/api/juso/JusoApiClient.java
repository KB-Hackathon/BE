package hackathon.kb.chakchak.global.utils.api.juso;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import hackathon.kb.chakchak.global.utils.api.juso.dto.JusoSearchResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JusoApiClient {

	@Value("${JUSO_ADMCD_BASE_URL}")
	private String baseUrl;

	@Value("${JUSO_ADMCD_API_KEY}")
	private String apiKey;

	// 간단 사용용 RestClient
	// 추후 Bean으로 등록해야 함
	private final RestClient rest = RestClient.create();

	// request 함수
	public String requestAdmCd(String roadNameAddress) {
		if (!StringUtils.hasText(roadNameAddress)) {
			return null;
		}

		String uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
			.queryParam("confmKey", apiKey)
			.queryParam("currentPage", 1)
			.queryParam("countPerPage", 1)
			.queryParam("keyword", roadNameAddress)
			.queryParam("resultType", "json")
			.build()
			.toUriString();
		log.info("JUSO request: {}", baseUrl);

		JusoSearchResponse body = rest.get()
			.uri(uri)
			.retrieve()
			.body(JusoSearchResponse.class);

		if (body == null || body.getResults() == null || body.getResults().getJuso() == null) {
			return null;
		}

		return body.getResults().getJuso().stream()
			.map(JusoSearchResponse.Juso::getAdmCd)
			.findFirst()
			.orElse(null);
	}
}
