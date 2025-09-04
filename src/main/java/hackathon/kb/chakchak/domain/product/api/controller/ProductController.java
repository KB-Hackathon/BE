package hackathon.kb.chakchak.domain.product.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.product.api.dto.ProductMetaRequest;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveRequest;
import hackathon.kb.chakchak.domain.product.service.ProductCommandService;
import hackathon.kb.chakchak.domain.product.service.ProductNarrativeService;
import hackathon.kb.chakchak.domain.product.service.dto.NarrativeResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
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

    @Operation(summary = "상품 등록", description = "상품 정보를 최종 등록합니다.")
    @PostMapping("/save")
    public ResponseEntity<Void> saveProduct(@RequestBody ProductSaveRequest req) {
        Long productId = productCommandService.saveProduct(req);
        return ResponseEntity.ok().build();
    }
}
