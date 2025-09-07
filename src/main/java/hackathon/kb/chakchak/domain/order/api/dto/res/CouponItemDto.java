package hackathon.kb.chakchak.domain.order.api.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CouponItemDto(
        @Schema(description = "쿠폰 ID")
        Long couponId,
        @Schema(description = "쿠폰 코드(UUID)")
        String couponUUID,
        @Schema(description = "쿠폰명(상품에서 정의)")
        String couponName,
        @Schema(description = "쿠폰 유효기간")
        LocalDate expiration,
        @Schema(description = "사용 여부")
        Boolean isUsed,
        @Schema(description = "주문 ID")
        Long orderId,
        @Schema(description = "사용자 ID")
        Long memberId,
        @Schema(description = "상품 ID")
        Long productId,
        @Schema(description = "공구글 제목")
        String productTitle,
        @Schema(description = "판매자 ID")
        Long sellerId,
        @Schema(description = "판매자 가게명")
        String storeName
) {}
