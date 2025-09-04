package hackathon.kb.chakchak.domain.product.api.controller;

import hackathon.kb.chakchak.domain.product.api.dto.ProductMetaRequest;
import hackathon.kb.chakchak.domain.product.service.ProductNarrativeService;
import hackathon.kb.chakchak.domain.product.service.dto.NarrativeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductNarrativeService productNarrativeService;

    @PostMapping(value = "/narrative", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NarrativeResult> makeNarrative(
            @RequestPart("meta") ProductMetaRequest meta,
            @RequestPart("images") List<MultipartFile> images
            // todo @AuthenticationPrincipal CustomUser customUser 추가
    ) {
        return ResponseEntity.ok(
                productNarrativeService.createNarrative(1L, meta, images)); // todo customUser 정보 받아오도록 수정
    }

}
