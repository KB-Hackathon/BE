package hackathon.kb.chakchak.domain.member.service;

import com.fasterxml.jackson.core.type.TypeReference;
import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.domain.member.service.dto.ApickBizDetailResponse;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import hackathon.kb.chakchak.global.util.ApiConnectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SellerService {
    private final MemberRepository memberRepository;
    private final MemberRolePromotionService promotionService;
    private final RestTemplate restTemplate;

    @Value("${apick.base-url}")
    private String baseUrl;

    @Value("${apick.auth-key}")
    private String authKey;

    @Transactional
    public Seller updateSellerFromApick(String bizNo, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.UNAUTHORIZED));
        if (member instanceof Seller seller) return seller;

        ApickBizDetailResponse.Data d = callApick(bizNo);
        return promotionService.promoteBuyerToSeller(member.getId(), d);
    }


    private ApickBizDetailResponse.Data callApick(String bizNo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("CL_AUTH_KEY", authKey);

        var form = new LinkedMultiValueMap<String, String>();
        form.add("biz_no", bizNo);

        var req = new HttpEntity<>(form, headers);

        ResponseEntity<String> raw = restTemplate.postForEntity(baseUrl, req, String.class);
        //log.info("[Apick RAW] status={} body={}", raw.getStatusCode(), raw.getBody());

        if (!raw.getStatusCode().is2xxSuccessful() || raw.getBody() == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST);
        }

        ApickBizDetailResponse parsed = ApiConnectionUtil.decodeJsonStringToDto(
                raw.getBody(),
                new TypeReference<ApickBizDetailResponse>() {
                },
                ApiConnectionUtil.CAMEL
        );

        if (parsed == null || !parsed.isSuccess() || parsed.getData() == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST);
        }
        return parsed.getData();
    }
}
