package hackathon.kb.chakchak.global.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiConnectionUtil {

	public static PropertyNamingStrategy CAMEL = PropertyNamingStrategy.LOWER_CAMEL_CASE;
	public static PropertyNamingStrategy SNAKE = PropertyNamingStrategy.SNAKE_CASE;

	/**
	 * URL 인코딩 문자열을 DTO로 변환하는 메서드
	 *
	 * @param encoded : url 인코딩된 문자열
	 * @param typeReference : 변환할 타입
	 * @param namingStrategy : 문자열의 네이밍 전략
	 * @return : 제네릭 클래스로 직렬화된 DTO 객체
	 * @param <T> : 리턴받을 TypeReference로 감싼 DTO 클래스
	 */
	public static <T> T decodeUrlStringToDto(String encoded, TypeReference<T> typeReference,
		PropertyNamingStrategy namingStrategy) {
		try {
			String decodedJson = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setPropertyNamingStrategy(namingStrategy);
			return objectMapper.readValue(decodedJson, typeReference);
		} catch (Exception e) {
			throw new RuntimeException("Decoding failed", e);
		}
	}

	/**
	 * json 문자열을 DTO로 변환하는 메서드
	 *
	 * @param jsonString : json 문자열
	 * @param typeReference : 변환할 타입
	 * @param namingStrategy : 문자열의 네이밍 전략
	 * @return : 제네릭 클래스로 직렬화된 DTO 객체
	 * @param <T> : 리턴받을 TypeReference로 감싼 DTO 클래스
	 */
	public static <T> T decodeJsonStringToDto(String jsonString, TypeReference<T> typeReference,
		PropertyNamingStrategy namingStrategy) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setPropertyNamingStrategy(namingStrategy);
			return objectMapper.readValue(jsonString, typeReference);
		} catch (Exception e) {
			throw new RuntimeException("Decoding failed", e);
		}
	}

	/**
	 * HttpURLConnection을 이용한 요청 GET 요청
	 *
	 * @param uri : URL 자동 인코딩 없이 경로 요청 가능
	 * @return 응답 문자열 (line 단위로 읽어서 append)
	 */
	public static String sendRequest(String uri) {

		HttpURLConnection connection = null;
		BufferedReader reader = null;

		try {
			URL url = new URL(uri);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod(HttpMethod.GET.name());
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			int responseCode = connection.getResponseCode();

			InputStream inputStream = (responseCode == 200)
				? connection.getInputStream()
				: connection.getErrorStream();

			reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

			StringBuilder responseBuilder = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				responseBuilder.append(line);
			}

			return responseBuilder.toString();

		} catch (IOException e) {
			log.error("HttpURLConnection 요청 중 예외 발생", e);
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignored) {
				}
			}
			if (connection != null) {
				connection.disconnect();
			}
		}

	}
}
