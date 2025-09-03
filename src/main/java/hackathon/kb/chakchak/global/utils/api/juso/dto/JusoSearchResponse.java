package hackathon.kb.chakchak.global.utils.api.juso.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JusoSearchResponse {
	private Results results;

	@Getter
	@NoArgsConstructor
	public static class Results {
		private List<Juso> juso;
	}

	@Getter
	@NoArgsConstructor
	public static class Juso {
		private String admCd;	// 행정동 코드
	}
}
