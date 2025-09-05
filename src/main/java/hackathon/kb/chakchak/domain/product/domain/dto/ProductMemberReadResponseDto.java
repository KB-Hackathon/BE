package hackathon.kb.chakchak.domain.product.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductMemberReadResponseDto(
	@Schema(description = "판매자 아이디", example = "1")
	Long memberId,
	@Schema(description = "회사명", example = "국민은행")
	String companyName,
	@Schema(description = "대표명", example = "이준범")
	String repName,
	@Schema(description = "회사 전화번호", example = "031-952-1714")
	String companyPhoneNumber,
	@Schema(description = "회사 주소", example = "고덕로 360")
	String sellerAddress

) {
}
