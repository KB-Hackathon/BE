package hackathon.kb.chakchak.domain.member.repository;

import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    @Query("""
        SELECT DISTINCT b
        FROM Buyer b
        LEFT JOIN FETCH b.orders o
        LEFT JOIN FETCH o.product p
        WHERE b.id = :buyerId
        """)
    Optional<Buyer> findByIdWithOrdersAndProduct(@Param("buyerId") Long buyerId);
}
