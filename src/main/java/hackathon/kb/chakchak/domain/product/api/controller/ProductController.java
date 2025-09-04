package hackathon.kb.chakchak.domain.product.api.controller;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.product.api.dto.ProductMetaRequest;
import hackathon.kb.chakchak.domain.product.api.dto.ProductSaveRequest;
import hackathon.kb.chakchak.domain.product.service.ProductCommandService;
import hackathon.kb.chakchak.domain.product.service.ProductNarrativeService;
import hackathon.kb.chakchak.domain.product.service.dto.NarrativeResult;
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
public class ProductController {
    private final ProductNarrativeService productNarrativeService;
    private final ProductCommandService productCommandService;

    @PostMapping(value = "/narrative", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NarrativeResult> makeNarrative(
            @RequestPart("meta") ProductMetaRequest meta,
            @RequestPart("images") List<MultipartFile> images,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        return ResponseEntity.ok(
                productNarrativeService.createNarrative(principal.getId(), meta, images));
    }

    @PostMapping("/save")
    public ResponseEntity<Void> saveProduct(@RequestBody ProductSaveRequest req) {
        Long productId = productCommandService.saveProduct(req);
        return ResponseEntity.ok().build();
    }
}
