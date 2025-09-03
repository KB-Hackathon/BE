package hackathon.kb.chakchak.global.utils.api.juso;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hackathon.kb.chakchak.global.utils.api.juso.dto.AdmCdResponse;

@RestController
@RequestMapping("/internal/juso")
public class AdmCodeLookupController {

	private final JusoApiClient jusoService;

	public AdmCodeLookupController(JusoApiClient jusoService) {
		this.jusoService = jusoService;
	}

	/**
	 * 도로명 주소 → 행정동 코드(admCd) 단건 변환
	 * 예: GET /internal/juso/admcd?roadAddress=서울특별시 강남구 테헤란로 123
	 */
	@GetMapping("/admcd")
	public ResponseEntity<AdmCdResponse> getAdmCd(@RequestParam("roadNameAddress") String roadNameAddress) {
		if (!StringUtils.hasText(roadNameAddress)) {
			return ResponseEntity.badRequest().build();
		}

		String admCd = jusoService.requestAdmCd(roadNameAddress);
		if (!StringUtils.hasText(admCd)) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(new AdmCdResponse(admCd));
	}
}
