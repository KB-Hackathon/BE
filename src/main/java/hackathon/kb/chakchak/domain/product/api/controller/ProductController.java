package hackathon.kb.chakchak.domain.product.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.product.api.dto.*;
import hackathon.kb.chakchak.domain.product.domain.dto.ProductReadResponseDto;
import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus;
import hackathon.kb.chakchak.domain.product.service.*;
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
@Tag(name = "상품 관련 API", description = "gpt 기반 문구 생성 / 상품 등록 / 상품 조회 API")
public class ProductController {
    private final ProductNarrativeService productNarrativeService;
    private final ProductCommandService productCommandService;
    private final OpenAIMultimodalNarrativeService narrativeService;
    private final ProductBasicService productBasicService;
    private final ProductService productService;

    @Operation(summary = "gpt 문구 받아오기", description = "상품 정보를 토대로 gpt 피드 문구 및 태그를 받아옵니다.")
    @PostMapping("/narrative")
    public BaseResponse<ProductMetaResponse> makeNarrative(
            @RequestBody ProductMetaRequest meta,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        return BaseResponse.OK(productNarrativeService.createNarrative(1L, meta));
    }

    @Operation(summary = "카테고리 / 모집상태 / 거래방식별 조회", description = "카테고리 / 모집상태 / 거래방식별 상품을 조회합니다. 10개를 반환하도록 하였습니다.")
    @GetMapping
    public BaseResponse<List<ProductReadResponseDto>> listByOption(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) Boolean isCoupon,
            @RequestParam(defaultValue = "0") int page) {
        return BaseResponse.OK(productBasicService.getProductsByOptions(category, status, isCoupon, page));
    }


    @Operation(summary = "상품 아이디 기반 조회", description = "상품 아이디를 기반으로 한 개 상품의 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public BaseResponse<ProductReadResponseDto> listByProductId(@PathVariable(name = "productId") Long productId){
        return BaseResponse.OK(productBasicService.getProductById(productId));
    }

    @Operation(summary = "상품 등록", description = "상품 정보를 최종 등록합니다.")
    @PostMapping("/save")
    public BaseResponse<ProductSaveResponse> saveProduct(
            @RequestBody ProductSaveRequest req,
            @AuthenticationPrincipal MemberPrincipal principal) {
        return BaseResponse.OK(productCommandService.saveProduct(principal.getId(), req));
    }

    @Operation(summary = "공동구매 현황 및 달성률", description = "상품 아이디를 기반으로 참여 인원과 달성률을 조회합니다.")
    @GetMapping("/progress/{productId}")
    public BaseResponse<ProductProgressResponseDto> getProductProgress(@PathVariable(name = "productId") Long productId){
        return BaseResponse.OK(productService.getProgressByProductId(productId));
    }
}
