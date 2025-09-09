package hackathon.kb.chakchak.domain.escrow.repository;

import hackathon.kb.chakchak.domain.escrow.domain.entity.Escrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscrowRepository extends JpaRepository<Escrow, Long> {
}