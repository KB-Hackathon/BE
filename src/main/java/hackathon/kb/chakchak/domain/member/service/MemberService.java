package hackathon.kb.chakchak.domain.member.service;

import hackathon.kb.chakchak.domain.member.api.dto.res.MemberProfileResponse;
import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.repository.MemberRepository;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import hackathon.kb.chakchak.global.response.ResponseCode;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberProfileResponse getMyProfile(Long memberId) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));

        return MemberProfileResponse.from(m);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));

        // soft delete
        m.setIsDeleted(true);
    }
}
