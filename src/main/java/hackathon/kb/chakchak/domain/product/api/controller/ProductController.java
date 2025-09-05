package hackathon.kb.chakchak.domain.product.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.product.api.dto.ProductMetaRequest;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveRequest;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveResponse;
import hackathon.kb.chakchak.domain.product.service.ProductCommandService;
import hackathon.kb.chakchak.domain.product.service.ProductNarrativeService;
import hackathon.kb.chakchak.domain.product.service.dto.NarrativeResult;
import hackathon.kb.chakchak.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "상품 API", description = "gpt 기반 문구 생성 / 상품 등록용 API")
public class ProductController {
    private final ProductNarrativeService productNarrativeService;
    private final ProductCommandService productCommandService;

    @Operation(summary = "gpt 문구 받아오기", description = "상품 정보를 토대로 gpt 피드 문구 및 태그를 받아옵니다.")
    @PostMapping("/narrative")
    public BaseResponse<NarrativeResult> makeNarrative(
            @RequestBody ProductMetaRequest meta,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        return BaseResponse.OK(
                productNarrativeService.createNarrative(principal.getId(), meta));
    }

    @Operation(summary = "상품 등록", description = "상품 정보를 최종 등록합니다.")
    @PostMapping("/save")
    public BaseResponse<ProductSaveResponse> saveProduct(
            @RequestBody ProductSaveRequest req,
            @AuthenticationPrincipal MemberPrincipal principal) {
        return BaseResponse.OK(productCommandService.saveProduct(principal.getId(), req));
    }
}
