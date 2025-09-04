package hackathon.kb.chakchak.domain.product.repository;

import hackathon.kb.chakchak.domain.product.domain.entity.InstaPrompt;
import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstaPromptRepository extends JpaRepository<InstaPrompt, Long> {
    Optional<InstaPrompt> findTopByCategory(Category category);
}
