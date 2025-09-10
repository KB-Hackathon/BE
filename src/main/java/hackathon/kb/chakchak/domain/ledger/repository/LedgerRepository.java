package hackathon.kb.chakchak.domain.ledger.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hackathon.kb.chakchak.domain.ledger.entity.LedgerEntry;
import hackathon.kb.chakchak.domain.ledger.entity.LedgerVoucher;
import hackathon.kb.chakchak.domain.ledger.entity.TransactionType;

public interface LedgerRepository extends JpaRepository<LedgerVoucher, Long> {

	@Query("""
       select le
       from LedgerEntry le
       join fetch le.ledgerVoucher v
       where v.type = :type
         and le.createdAt >= :start
         and le.createdAt < :end
       order by le.ledgerEntryId desc
       """)
	List<LedgerEntry> findAllOnYesterdayByVoucherType(
		@Param("type") TransactionType type,
		@Param("start") LocalDateTime start,
		@Param("end") LocalDateTime end
	);

	@Query("""
        select min(le.ledgerEntryId)
        from LedgerEntry le
        where le.createdAt >= :start and le.createdAt < :end
    """)
	Long findMinIdInRange(@Param("start") LocalDateTime start,
		@Param("end") LocalDateTime end);

	@Query("""
        select max(le.ledgerEntryId)
        from LedgerEntry le
        where le.createdAt >= :start and le.createdAt < :end
    """)
	Long findMaxIdInRange(@Param("start") LocalDateTime start,
		@Param("end") LocalDateTime end);
}
