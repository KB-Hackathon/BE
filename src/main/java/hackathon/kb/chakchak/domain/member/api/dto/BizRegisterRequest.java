package hackathon.kb.chakchak.domain.member.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BizRegisterRequest {
    @NotBlank(message = "사업자등록번호는 필수입니다.")
    private String bizNo;
}