package hackathon.kb.chakchak.domain.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.kb.chakchak.domain.product.service.dto.NarrativeResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIMultimodalNarrativeService {
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.model}")
    private String model;

    public NarrativeResult generateNarrativeWithImageUrls(
            String systemInstruction,
            String userText,
            List<String> imageUrls
    ) {
        // user 컨텐츠 파츠 구성
        List<Map<String, Object>> userParts = new ArrayList<>();

        if (userText != null && !userText.isBlank()) {
            userParts.add(Map.of(
                    "type", "input_text",
                    "text", userText
            ));
        }
        if (imageUrls != null) {
            for (String url : imageUrls) {
                if (url == null || url.isBlank()) continue;
                userParts.add(Map.of(
                        "type", "input_image",
                        "image_url", url
                ));
            }
        }

        String raw = callOnce(systemInstruction, userParts);
        return tryParseJsonResult(raw);
    }

    /**
     * Responses API 호출
     * - instructions: system 지침
     * - input: 멀티모달 파츠(텍스트/이미지)
     */
    private String callOnce(String systemInstruction, List<Map<String, Object>> userContentBlocks) {
        try {
            Map<String, Object> req = new LinkedHashMap<>();
            req.put("model", model);
            req.put("temperature", 0.7);
            req.put("max_output_tokens", 800);

            if (systemInstruction != null && !systemInstruction.isBlank()) {
                req.put("instructions", systemInstruction);
            }

            // Responses API 멀티모달 입력 형식
            // input: [{ role: "user", content: [ {type: input_text|input_image, ...}, ... ] }]
            List<Map<String, Object>> input = List.of(
                    Map.of(
                            "role", "user",
                            "content", userContentBlocks
                    )
            );
            req.put("input", input);

            String json = objectMapper.writeValueAsString(req);

            // 요청 원문 로그 (API 키 마스킹)
            log.info("[OpenAI REQUEST] url={} model={} body={}", apiUrl, model, json);


            RequestBody body = RequestBody.create(
                    json, MediaType.parse("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response resp = okHttpClient.newCall(request).execute()) {
                String rid = resp.header("x-request-id");
                String respText = resp.body() != null
                        ? new String(resp.body().bytes(), StandardCharsets.UTF_8)
                        : "";

                // 응답 원문 로그
                log.info("[OpenAI RESPONSE] status={} rid={} body={}", resp.code(), rid, respText);

                if (!resp.isSuccessful()) {
                    log.error("OpenAI API {} error (request-id: {}): {}", resp.code(), rid, respText);
                    throw new RuntimeException("OpenAI API 호출 실패: " + resp.code());
                }

                // Responses API 파싱 (output[].content[].text)
                String text = tryParseResponsesApi(respText);
                if (text != null) return text;

                log.warn("Unknown response format: {}", respText);
                return "";
            }
        } catch (Exception e) {
            log.error("OpenAI 멀티모달 호출 실패: {}", e.getMessage(), e);
            throw new RuntimeException("멀티모달 분석 실패", e);
        }
    }

    /**
     * Responses API 응답 파서
     * 형식: { "output": [ { "content": [ { "type": "output_text", "text": "..." } ] } ] }
     */
    private String tryParseResponsesApi(String respText) {
        try {
            JsonNode root = objectMapper.readTree(respText);
            JsonNode output = root.path("output");
            if (!output.isArray() || output.isEmpty()) return null;

            JsonNode out0 = output.get(0);
            JsonNode content = out0.path("content");
            if (!content.isArray() || content.isEmpty()) return null;

            // 첫 output_text만 추출
            for (JsonNode c : content) {
                if ("output_text".equals(c.path("type").asText())) {
                    String text = c.path("text").asText(null);
                    if (text != null && !text.isBlank()) return text.trim();
                }
            }
            return null;
        } catch (Exception ignore) {
            return null;
        }
    }

    // JSON 파서
    private NarrativeResult tryParseJsonResult(String respText) {
        String text = tryParseResponsesApi(respText);
        if (text == null || text.isBlank()) {
            text = respText; // 모델이 곧장 JSON만 준 경우 대비
        }
        try {
            JsonNode root = objectMapper.readTree(text.trim());
            String caption = Optional.ofNullable(root.path("caption").asText(null)).orElse("").trim();

            String hashtagsRaw = root.path("hashtags").asText(null);
            List<String> hashtags = parseHashtags(hashtagsRaw);

            return new NarrativeResult(caption, hashtags);
        } catch (Exception e) {
            log.warn("JSON 파싱 실패, 원문 반환 시도: {}", e.toString());
            return new NarrativeResult(text == null ? "" : text.trim(), Collections.emptyList());
        }
    }

    private List<String> parseHashtags(String hashtagsRaw) {
        if (hashtagsRaw == null || hashtagsRaw.isBlank()) {
            return new ArrayList<>();
        }

        // 공백으로 분리, "#" 없는 토큰은 무시
        String[] tokens = hashtagsRaw.trim().split("\\s+");
        List<String> hashtags = new ArrayList<>();

        for (String token : tokens) {
            if (!token.isBlank()) {
                // 앞에 # 없으면 붙여줌
                if (!token.startsWith("#")) {
                    token = "#" + token;
                }
                hashtags.add(token);
            }
        }

        return hashtags;
    }
}