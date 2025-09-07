package hackathon.kb.chakchak.domain.order.api.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record MyCouponListResponse(
        @Schema(description = "구매자가 보유한 쿠폰 목록")
        List<CouponItemDto> coupons
) {}
