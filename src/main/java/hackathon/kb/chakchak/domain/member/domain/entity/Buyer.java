package hackathon.kb.chakchak.domain.member.domain.entity;

import java.util.List;

import hackathon.kb.chakchak.domain.review.domain.entity.Review;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("BUYER")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Buyer extends Member {

	@OneToMany(mappedBy = "reviewer", fetch = FetchType.LAZY)
	List<Review> reviews;
}
