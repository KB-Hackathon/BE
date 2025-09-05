package hackathon.kb.chakchak.domain.product.api.controller;

import hackathon.kb.chakchak.domain.product.domain.dto.ProductReadResponseDto;
import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import hackathon.kb.chakchak.domain.product.service.OpenAIMultimodalNarrativeService;
import hackathon.kb.chakchak.domain.product.service.ProductBasicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
@Tag(name = "상품 조회 API", description = "상품 조회용 API")
public class ProductController {
    private final OpenAIMultimodalNarrativeService narrativeService;
    private final ProductBasicService productBasicService;

    @PostMapping("/narrative")
    public String makeNarrative(@RequestBody NarrativeRequest req) {
        return narrativeService.generateNarrativeWithImageUrls(
                req.getSystemInstruction(),
                req.getUserText(),
                req.getImageUrls()
        );
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
    }
}
