package hackathon.kb.chakchak.domain.product.repository;

import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    void deleteByProduct(Product product);
}
