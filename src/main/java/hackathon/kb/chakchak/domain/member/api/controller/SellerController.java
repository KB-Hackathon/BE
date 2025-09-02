package hackathon.kb.chakchak.domain.member.api.controller;

import hackathon.kb.chakchak.domain.member.api.dto.BizRegisterRequest;
import hackathon.kb.chakchak.domain.member.api.dto.BizRegisterResponse;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.member.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @PostMapping("/register")
    public ResponseEntity<BizRegisterResponse> register(
            @Valid @RequestBody BizRegisterRequest req
            // todo @AuthenticationPrincipal CustomUser customUser 추가
    ) {
        Seller saved = sellerService.updateSellerFromApick(req.getBizNo(), 4L); /// todo customUser 정보 받아오도록 수정
        return ResponseEntity.ok(BizRegisterResponse.from(saved));
    }
}
