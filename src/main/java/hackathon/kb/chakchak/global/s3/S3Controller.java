package hackathon.kb.chakchak.global.s3;

import hackathon.kb.chakchak.global.response.BaseResponse;
import java.net.URL;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import hackathon.kb.chakchak.global.s3.service.S3StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
@Tag(name = "S3 Test API", description = "S3 업로드/조회/삭제 테스트용 API")
public class S3Controller {

	private final S3StorageService s3StorageService;

	@Operation(summary = "단일 이미지 업로드", description = "Multipart 단일 파일을 S3에 업로드합니다.")
	@PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<UrlResponse> uploadImage(
		@RequestPart("file") MultipartFile file,
		@RequestParam(value = "dirPrefix", required = false) String dirPrefix
	) {
		URL url = StringUtils.hasText(dirPrefix)
			? s3StorageService.uploadImages(List.of(file), dirPrefix).get(0) // prefix 적용해서 업로드
			: s3StorageService.uploadImages(file);                           // 단일 업로드 메서드 사용

		return BaseResponse.OK(new UrlResponse(url.toString()));
	}

	@Operation(summary = "다중 이미지 업로드", description = "Multipart 여러 파일을 S3에 업로드합니다.")
	@PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<UrlListResponse> uploadImages(
		@RequestPart("files") List<MultipartFile> files,
		@RequestParam(value = "dirPrefix", required = false) String dirPrefix
	) {
		List<URL> urls = StringUtils.hasText(dirPrefix)
			? s3StorageService.uploadImages(files, dirPrefix) // prefix 적용
			: s3StorageService.uploadImages(files);           // prefix 없이

		return BaseResponse.OK(new UrlListResponse(urls.stream().map(URL::toString).toList()));
	}

	@Operation(summary = "업로드된 이미지 URL 조회", description = "S3 key로 접근 가능한 URL을 반환합니다.")
	@GetMapping("/image-url")
	public BaseResponse<UrlResponse> getImageUrl(@RequestParam("key") String key) {
		URL url = s3StorageService.getImageUrl(key);
		return BaseResponse.OK(new UrlResponse(url.toString()));
	}

	@Operation(summary = "이미지 삭제", description = "S3에 업로드된 객체를 key로 삭제합니다.")
	@DeleteMapping("/image")
	public BaseResponse<Void> deleteImage(@RequestParam("key") String key) {
		s3StorageService.deleteImage(key);
		return BaseResponse.OK();
	}

	// --- 응답 DTO (간단히 record로) ---
	public record UrlResponse(String url) {}
	public record UrlListResponse(List<String> urls) {}
}
