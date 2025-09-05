package hackathon.kb.chakchak.domain.member.api.dto.req;

import hackathon.kb.chakchak.domain.member.domain.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalInfoRequest {
    @Schema(description = "추가 정보 기입 token")
    @NotBlank
    private String signupToken;

    @Schema(description = "유저 실명", example = "홍길동")
    @NotBlank
    private String name; // 이름 (필수)

    @Schema(description = "유저 나이", example = "25")
    @NotNull
    private Short age; // 나이 (필수)

    @Schema(description = "성별", example = "MALE")
    @NotNull(message = "gender는 필수입니다. [MALE, FEMALE] 중 하나")
    private Gender gender; // 성별 (필수)

    @Schema(description = "배송 받을 주소", example = "서울특별시 광진구 능동로")
    @NotBlank
    private String address; // 도로명 주소 (필수)

    @Schema(description = "전화번호", example = "01011112222")
    private String phoneNumber; // 전화번호 (선택)
}
