package hackathon.kb.chakchak.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {
	Optional<Buyer> findById(Long memberId);
}
