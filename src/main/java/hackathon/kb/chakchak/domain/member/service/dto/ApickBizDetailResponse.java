package hackathon.kb.chakchak.domain.member.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApickBizDetailResponse {

    @JsonProperty("api")
    private Api api;

    @JsonProperty("data")
    private Data data;

    public boolean isSuccess() {
        return api != null && Boolean.TRUE.equals(api.getSuccess());
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Api {
        private Boolean success;
        private Integer cost;
        private Integer ms;
        @JsonProperty("pl_id")
        private Long plId;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        @JsonProperty("회사명")
        private String companyName;
        @JsonProperty("사업자등록번호")
        private String bizNo;
        @JsonProperty("대표명")
        private String repName;
        @JsonProperty("업태")
        private String industryBusinessType;
        @JsonProperty("종목")
        private String bizDescription;
        @JsonProperty("전화번호")
        private String phoneNumber;
        @JsonProperty("우편번호")
        private String zipCode;
        @JsonProperty("도로명주소")
        private String roadNameAddress;
        @JsonProperty("표준산업분류(노동부) 업종코드")
        private String companyClassificationCode;
    }
}
