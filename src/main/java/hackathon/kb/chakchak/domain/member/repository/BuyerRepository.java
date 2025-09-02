package hackathon.kb.chakchak.domain.member.repository;

import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {
}
