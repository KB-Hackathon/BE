package hackathon.kb.chakchak.domain.product.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.product.api.dto.ProductMetaRequest;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveRequest;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveResponse;
import hackathon.kb.chakchak.domain.product.domain.dto.ProductReadResponseDto;
import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import hackathon.kb.chakchak.domain.product.service.OpenAIMultimodalNarrativeService;
import hackathon.kb.chakchak.domain.product.service.ProductBasicService;
import hackathon.kb.chakchak.domain.product.service.ProductCommandService;
import hackathon.kb.chakchak.domain.product.service.ProductNarrativeService;
import hackathon.kb.chakchak.domain.product.service.dto.NarrativeResult;
import hackathon.kb.chakchak.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "상품 관련 API", description = "gpt 기반 문구 생성 / 상품 등록용 API")
public class ProductController {
    private final ProductNarrativeService productNarrativeService;
    private final ProductCommandService productCommandService;
    private final OpenAIMultimodalNarrativeService narrativeService;
    private final ProductBasicService productBasicService;

    @Operation(summary = "gpt 문구 받아오기", description = "상품 정보를 토대로 gpt 피드 문구 및 태그를 받아옵니다.")
    @PostMapping("/narrative")
    public BaseResponse<NarrativeResult> makeNarrative(
            @RequestBody ProductMetaRequest meta,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        return BaseResponse.OK(
                productNarrativeService.createNarrative(principal.getId(), meta));
    }

    @Operation(summary = "카테고리별 조회", description = "카테고리별 상품을 조회합니다. 10개를 반환하도록 하였습니다.")
    @GetMapping("/category/{category}")
    public List<ProductReadResponseDto> listByCategory(
        @PathVariable Category category,
        @RequestParam(defaultValue = "0") int page) {
        return productBasicService.getProductsByCategory(category, page);
    }

    @Operation(summary = "상품 아이디 기반 조회", description = "상품 아이디를 기반으로 한 개 상품의 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ProductReadResponseDto listByCategory(@PathVariable(name = "productId") Long productId){
        return productBasicService.getProductById(productId);
    }

    @Operation(summary = "상품 등록", description = "상품 정보를 최종 등록합니다.")
    @PostMapping("/save")
    public BaseResponse<ProductSaveResponse> saveProduct(
            @RequestBody ProductSaveRequest req,
            @AuthenticationPrincipal MemberPrincipal principal) {
        return BaseResponse.OK(productCommandService.saveProduct(principal.getId(), req));
    }
}
