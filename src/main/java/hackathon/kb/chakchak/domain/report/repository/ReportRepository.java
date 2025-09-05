package hackathon.kb.chakchak.domain.report.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hackathon.kb.chakchak.domain.report.domain.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
	Optional<Report> findBySellerId(Long sellerId);
}
