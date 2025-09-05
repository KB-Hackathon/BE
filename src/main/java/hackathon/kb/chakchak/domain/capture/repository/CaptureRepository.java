package hackathon.kb.chakchak.domain.capture.repository;

import hackathon.kb.chakchak.domain.capture.domain.Capture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaptureRepository extends JpaRepository<Capture, Long> {
}
