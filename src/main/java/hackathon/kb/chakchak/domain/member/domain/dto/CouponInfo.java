package hackathon.kb.chakchak.domain.member.domain.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponInfo {

	private String code;
	private LocalDate expiration;
	private String productName;
	private Short quantity;
}
