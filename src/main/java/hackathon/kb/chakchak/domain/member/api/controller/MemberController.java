package hackathon.kb.chakchak.domain.member.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.member.api.dto.res.MemberProfileResponse;
import hackathon.kb.chakchak.domain.member.service.MemberService;
import hackathon.kb.chakchak.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 로그인한 사옹자 정보 가져오기
    @GetMapping("/me")
    public BaseResponse<MemberProfileResponse> getMyProfile(@AuthenticationPrincipal MemberPrincipal principal) {
        Long memberId = principal.getId();
        return BaseResponse.OK(memberService.getMyProfile(memberId));
    }
}