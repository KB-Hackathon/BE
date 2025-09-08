package hackathon.kb.chakchak.domain.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hackathon.kb.chakchak.domain.ledger.entity.LedgerCode;

@Repository
public interface LedgerCodeRepository extends JpaRepository<LedgerCode, Long> {
}
