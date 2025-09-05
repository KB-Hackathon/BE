package hackathon.kb.chakchak.domain.member.service;

import com.fasterxml.jackson.core.type.TypeReference;
import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.domain.member.service.dto.ApickBizDetailResponse;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import hackathon.kb.chakchak.global.util.ApiConnectionUtil;
import hackathon.kb.chakchak.global.utils.api.juso.JusoApiClient;
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
    private final JusoApiClient jusoApiClient;

    @Value("${apick.base-url}")
    private String baseUrl;

    @Value("${apick.auth-key}")
    private String authKey;

    @Transactional
    public Seller updateSellerFromApick(String bizNo, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.UNAUTHORIZED));
        if (member instanceof Seller seller) return seller;

        // 1. Apick API -> 사업자 정보
        ApickBizDetailResponse.Data d;
        try {
            d = callApick(bizNo);
        } catch (Exception e) {
            log.error("Apick API 호출 실패: bizNo={}, memberId={}", bizNo, memberId, e);
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        // 2. admCd
        String roadNameAddress = d.getRoadNameAddress();
        if (roadNameAddress == null || roadNameAddress.isBlank()) {
            log.warn("도로명 주소 없음 bizNo={}, memberId={}", bizNo, memberId);
            throw new BusinessException(ResponseCode.BAD_REQUEST);
        }

        String admCd;
        try {
            admCd = jusoApiClient.requestAdmCd(roadNameAddress);
        } catch (Exception e) {
            log.error("Juso 호출 실패 memberId={}, road={}", memberId, roadNameAddress, e);
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (admCd == null || admCd.isBlank()) {
            log.warn("Juso 응답에 admCd 없음 memberId={}, road={}", memberId, roadNameAddress);
            throw new BusinessException(ResponseCode.BAD_REQUEST);
        }

        // 3. promoition
        try {
            return promotionService.promoteBuyerToSeller(member.getId(), d, admCd);
        } catch (Exception e) {
            log.error("승급 실패 memberId={}", memberId, e);
            throw new BusinessException(ResponseCode.CONFLICT);
        }
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
