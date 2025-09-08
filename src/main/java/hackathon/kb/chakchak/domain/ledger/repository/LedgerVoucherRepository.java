package hackathon.kb.chakchak.domain.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hackathon.kb.chakchak.domain.ledger.entity.LedgerVoucher;

@Repository
public interface LedgerVoucherRepository extends JpaRepository<LedgerVoucher, Long> {
	boolean existsByVoucherNo(String voucherNo);
}
