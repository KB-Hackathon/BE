package hackathon.kb.chakchak.domain.order.api.dto.req;

import lombok.Data;

@Data
public class CouponOrderReq {

	private Long productId;
	private Short quantity;
}
