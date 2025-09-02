package hackathon.kb.chakchak.domain.member.api.dto;

import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BizRegisterResponse {
    private Long id;
    private String bizNo;
    private String companyName;
    private String repName;
    private String industryBusinessType;
    private String bizDescription;
    private String companyPhoneNumber;
    private String zipCode;
    private String roadNameAddress;
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