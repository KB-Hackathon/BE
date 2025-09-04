package hackathon.kb.chakchak.domain.member.service;

import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.domain.member.service.dto.ApickBizDetailResponse;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberRolePromotionService {
    private final MemberRepository memberRepository;
    private final EntityManager em;

    @Transactional
    public Seller promoteBuyerToSeller(Long memberId, ApickBizDetailResponse.Data d, String admCd) {
        int rows = memberRepository.promoteBuyerToSeller(
                memberId,
                digits(d.getBizNo(), 20),
                d.getCompanyName(),
                d.getRepName(),
                d.getIndustryBusinessType(),
                d.getBizDescription(),
                digits(d.getPhoneNumber(), 11),
                digits(d.getZipCode(), 5),
                d.getRoadNameAddress(),
                digits(d.getCompanyClassificationCode(), 6),
                admCd
        );
        if (rows != 1) throw new BusinessException(ResponseCode.CONFLICT);

        em.flush();
        em.clear();

        Member reloaded = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));

        if (!(reloaded instanceof Seller seller)) {
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        return seller;
    }

    private String digits(String s, int max) {
        if (s == null) return null;
        String only = s.replaceAll("\\D", "");
        String tmp = only.length() > max ? only.substring(0, max) : only;
        System.out.println("tmp = " + tmp);
        return tmp;
    }
}
