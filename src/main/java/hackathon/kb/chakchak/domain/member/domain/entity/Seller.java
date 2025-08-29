package hackathon.kb.chakchak.domain.member.domain.entity;

import java.util.List;

import hackathon.kb.chakchak.domain.review.domain.entity.Review;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("SELLER")
@Getter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Seller extends Member {

	@OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
	private List<Review> reviews;

	@Column(length = 20)
	private String bizNo; 					// (seller) 사업자등록번호

	@Column(length = 100)
	private String companyName; 			// (seller) 회사명

	@Column(length = 50)
	private String repName; 				// (seller) 대표명

	@Column(length = 50)
	private String industryBusinessType; 	// (seller) 업종

	@Column(length = 255)
	private String bizDescription; 			// (seller) 종목 설명

	@Column(length = 11)
	private String companyPhoneNumber; 		// (seller) 회사 전화번호

	@Column(length = 5)
	private String zipCode; 				// (seller) 우편번호

	@Column(length = 50)
	private String roadNameAddress; 		// (seller) 도로명주소

	@Column(length = 6)
	private String companyClassificationCode; // (seller) 표준산업분류 업종코드

	@Column(length = 25)
	private String accountNumber; 			// (seller) 판매자 계좌 번호
}