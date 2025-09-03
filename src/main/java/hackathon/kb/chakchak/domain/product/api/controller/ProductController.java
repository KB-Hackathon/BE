package hackathon.kb.chakchak.domain.product.api.controller;

import hackathon.kb.chakchak.domain.product.service.OpenAIMultimodalNarrativeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final OpenAIMultimodalNarrativeService narrativeService;

    @PostMapping("/narrative")
    public String makeNarrative(@RequestBody NarrativeRequest req) {
        return narrativeService.generateNarrativeWithImageUrls(
                req.getSystemInstruction(),
                req.getUserText(),
                req.getImageUrls()
        );
    }

    // 임시용
    @Data
    public static class NarrativeRequest {
        private String systemInstruction;
        private String userText;
        private List<String> imageUrls;
    }
}
