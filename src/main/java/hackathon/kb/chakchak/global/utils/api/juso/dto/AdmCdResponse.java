package hackathon.kb.chakchak.global.utils.api.juso.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdmCdResponse {
	private String admCd;

	public AdmCdResponse(String admCd) {
		this.admCd = admCd;		// 행정동 코드
	}
}
