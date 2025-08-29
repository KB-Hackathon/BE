package hackathon.kb.chakchak.domain.review.domain.entity;

import java.math.BigDecimal;
import java.util.List;

import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;
import hackathon.kb.chakchak.domain.member.domain.entity.Member;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor @AllArgsConstructor @Builder
public class Review {

	@Id
	@Column(name = "review_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = " reviewer_id", nullable = false)
	private Buyer reviewer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private Seller seller;

	@Column(nullable = false)
	private String content;					// 리뷰 내용

	@Column(precision = 1, scale = 1)
	private BigDecimal rate;				// 별점
}
