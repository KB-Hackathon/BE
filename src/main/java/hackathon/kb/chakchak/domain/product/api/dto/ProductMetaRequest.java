package hackathon.kb.chakchak.domain.product.api.dto;

import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import lombok.Data;

@Data
public class ProductMetaRequest {
    private String title;
    private Category category;
    private String description;
}

