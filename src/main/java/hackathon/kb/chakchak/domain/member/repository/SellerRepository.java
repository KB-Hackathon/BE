package hackathon.kb.chakchak.domain.member.repository;

import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    @Query("""
        SELECT DISTINCT s
        FROM Seller s
        LEFT JOIN FETCH s.products p
        WHERE s.id = :sellerId
            AND TYPE(s) = Seller
    """)
    Optional<Seller> findByIdWithProducts(@Param("sellerId") Long sellerId);
}
