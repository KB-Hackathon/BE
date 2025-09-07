package hackathon.kb.chakchak.domain.member.api.dto.res;

import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BizRegisterResponse {
    private Long id;

    @Schema(description = "사업자 등록 번호", example = "1234567890")
    private String bizNo;

    @Schema(description = "회사명", example = "성남_씨유")
    private String companyName;

    @Schema(description = "대표명", example = "김서현")
    private String repName;

    @Schema(description = "업종", example = "PC영업시설")
    private String industryBusinessType;

    @Schema(description = "종목 설명", example = "여성청소년생필품")
    private String bizDescription;

    @Schema(description = "회사 전화번호", example = "07088069787")
    private String companyPhoneNumber;

    @Schema(description = "우편 번호", example = "16930")
    private String zipCode;

    @Schema(description = "도로명 주소", example = "경기도 용인시 수지구 상현2동 861")
    private String roadNameAddress;

    @Schema(description = "표준산업분류 업종코드", example = "47912")
    private String companyClassificationCode;

    public static BizRegisterResponse from(Seller s) {
        return BizRegisterResponse.builder()
                .id(s.getId())
                .bizNo(s.getBizNo())
                .companyName(s.getCompanyName())
                .repName(s.getRepName())
                .industryBusinessType(s.getIndustryBusinessType())
                .bizDescription(s.getBizDescription())
                .companyPhoneNumber(s.getCompanyPhoneNumber())
                .zipCode(s.getZipCode())
                .roadNameAddress(s.getRoadNameAddress())
                .companyClassificationCode(s.getCompanyClassificationCode())
                .build();
    }
}