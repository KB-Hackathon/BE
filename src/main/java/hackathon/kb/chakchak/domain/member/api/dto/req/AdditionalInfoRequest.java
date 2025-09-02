package hackathon.kb.chakchak.domain.member.api.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalInfoRequest {
    @NotBlank
    private String signupToken;

    @NotBlank
    private String name; // 이름 (필수)

    @NotNull
    private Short age; // 나이 (필수)

    @NotBlank
    private String address; // 도로명 주소 (필수)

    private String phoneNumber; // 전화번호 (선택)

    private String image; // 프로필 이미지 (선택)
}
