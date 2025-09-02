package hackathon.kb.chakchak.domain.member.repository;

import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
}
