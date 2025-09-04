package hackathon.kb.chakchak.domain.product.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NarrativeResult {
    private final String caption;
    private final List<String> hashtags;
}
