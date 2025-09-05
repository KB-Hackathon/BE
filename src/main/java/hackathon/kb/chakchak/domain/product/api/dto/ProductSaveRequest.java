package hackathon.kb.chakchak.domain.product.api.dto;

import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductSaveRequest {
    @Schema(description = "상품명", example = "콜드브루 몰트 크림")
    private String title;

    @Schema(description = "상품 카테고리", example = "카페")
    private Category category;

    @Schema(description = "상품 이미지 url", example = "[\"https://chakchak-img/0.png\", \"https://chakchak-img/1.png\"]")
    private List<String> images;

    @Schema(description = "상품 상세 설명", example = "여름의 시작을 알리는 특별한 순간...")
    private String description;

    @Schema(description = "상품 태그 목록", example = "[\"#스타벅스\", \"#콜드브루\", \"#여름음료\"]")
    private List<String> tags;

    @Schema(description = "상품 가격", example = "4800")
    private BigDecimal price;

    @Schema(description = "쿠폰 사용 가능 여부", example = "true")
    private Boolean isCoupon;

    @Schema(description = "상품 쿠폰명", example = "콜드브루 몰트 1회 사용권")
    private String couponName;

    @Schema(description = "쿠폰 유효기간 일시", example = "2025-10-05T00:00:00")
    private LocalDateTime couponExpiration;

    @Schema(description = "목표 수량", example = "100")
    private Short targetAmount;

    @Schema(description = "모집 시작일시", example = "2025-09-05T00:00:00")
    private LocalDateTime recruitmentStartPeriod;

    @Schema(description = "모집 종료일시", example = "2025-09-30T23:59:59")
    private LocalDateTime recruitmentEndPeriod;
}

