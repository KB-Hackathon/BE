package hackathon.kb.chakchak.domain.escrow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hackathon.kb.chakchak.domain.escrow.domain.entity.Escrow;

@Repository
public interface EscrowRepository extends JpaRepository<Escrow, Long> {
}
