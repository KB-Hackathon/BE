package hackathon.kb.chakchak.domain.member.api.controller;

import hackathon.kb.chakchak.domain.member.api.dto.res.MemberProfileResponse;
import hackathon.kb.chakchak.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
//    @GetMapping("/me")
//    public ResponseEntity<MemberProfileResponse> getMyProfile(@AuthenticationPrincipal Long sub) {
//        Long memberId = Long.valueOf(sub);
//        return ResponseEntity.ok(memberService.getMyProfile(memberId));
//    }

    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> getMyProfile(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(memberService.getMyProfile(memberId));
    }


    // TODO: 권한으로 403 반환
    // @PreAuthorize("hasAnyRole('SELLER')")
}