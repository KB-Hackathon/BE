package hackathon.kb.chakchak.domain.order.api.dto.res;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CouponOrderRes {
	private String couponCode;
	private LocalDate expireDate;
}
