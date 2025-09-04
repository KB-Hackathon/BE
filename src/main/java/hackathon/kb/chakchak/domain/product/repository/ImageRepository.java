package hackathon.kb.chakchak.domain.product.repository;

import hackathon.kb.chakchak.domain.product.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
