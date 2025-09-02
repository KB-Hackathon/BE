package hackathon.kb.chakchak.domain.member.service;

import hackathon.kb.chakchak.domain.member.api.dto.res.MemberProfileResponse;
import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import hackathon.kb.chakchak.global.response.ResponseCode;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberProfileResponse getMyProfile(Long memberId) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));

        return MemberProfileResponse.from(m);
    }
}
