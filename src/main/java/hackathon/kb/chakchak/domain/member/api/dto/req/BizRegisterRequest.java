package hackathon.kb.chakchak.domain.member.api.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BizRegisterRequest {
    @Schema(description = "사업자 등록 번호", example = "1234567890")
    @NotBlank(message = "사업자등록번호는 필수입니다.")
    private String bizNo;
}