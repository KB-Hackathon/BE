package hackathon.kb.chakchak.domain.member.api.dto.res;

import hackathon.kb.chakchak.domain.order.domain.dto.OrderResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record BuyerOrderListResponse(
        @Schema(description = "구매자가 참여한 공동구매(주문+상품) 리스트")
        List<OrderResponseDto> orders
) {}