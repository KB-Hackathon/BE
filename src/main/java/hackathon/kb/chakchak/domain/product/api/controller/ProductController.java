package hackathon.kb.chakchak.domain.product.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.product.api.dto.ProductMetaRequest;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveRequest;
import hackathon.kb.chakchak.domain.product.domain.dto.ProductReadResponseDto;
import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import hackathon.kb.chakchak.domain.product.service.ProductBasicService;
import hackathon.kb.chakchak.domain.product.service.ProductCommandService;
import hackathon.kb.chakchak.domain.product.service.ProductNarrativeService;
import hackathon.kb.chakchak.domain.product.service.dto.NarrativeResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
@Tag(name = "상품 조회 API", description = "상품 조회용 API")
public class ProductController {
  
    private final OpenAIMultimodalNarrativeService narrativeService;
    private final ProductBasicService productBasicService;
  
@RequestMapping("/api/products")
@Tag(name = "상품 API", description = "gpt 기반 문구 생성 / 상품 등록용 API")
public class ProductController {
    private final ProductNarrativeService productNarrativeService;
    private final ProductCommandService productCommandService;

    @Operation(summary = "gpt 문구 받아오기", description = "상품 정보를 토대로 gpt 피드 문구 및 태그를 받아옵니다.")
    @PostMapping(value = "/narrative", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NarrativeResult> makeNarrative(
            @RequestPart("meta") ProductMetaRequest meta,
            @RequestPart("images") List<MultipartFile> images,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        return ResponseEntity.ok(
                productNarrativeService.createNarrative(principal.getId(), meta, images));
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

    // 임시용
    @Data
    public static class NarrativeRequest {
        private String systemInstruction;
        private String userText;
        private List<String> imageUrls;
    @Operation(summary = "상품 등록", description = "상품 정보를 최종 등록합니다.")
    @PostMapping("/save")
    public ResponseEntity<Void> saveProduct(@RequestBody ProductSaveRequest req) {
        Long productId = productCommandService.saveProduct(req);
        return ResponseEntity.ok().build();
    }
}
