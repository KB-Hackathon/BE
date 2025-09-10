package hackathon.kb.chakchak.domain.escrow.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hackathon.kb.chakchak.domain.escrow.domain.entity.Escrow;

public interface EscrowRepositoryJun extends JpaRepository<Escrow, Long> {
	@Query("""
        SELECT e
        FROM Escrow e
        WHERE function('date', e.createdAt) = :targetDate
    """)
	List<Escrow> findAllByCreatedDate(@Param("targetDate") java.time.LocalDate targetDate);
}
