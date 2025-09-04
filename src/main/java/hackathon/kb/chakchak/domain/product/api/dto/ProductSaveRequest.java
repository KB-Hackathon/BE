package hackathon.kb.chakchak.domain.product.api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductSaveRequest {
    private Long productId;
    private String description;
    private List<String> tags; // 태그 문자열 리스트로 받아서 Entity로 변환
    private Long price;
    private Boolean isCoupon;
    private Short targetAmount;
    private LocalDateTime recruitmentStartPeriod;
    private LocalDateTime recruitmentEndPeriod;
}
