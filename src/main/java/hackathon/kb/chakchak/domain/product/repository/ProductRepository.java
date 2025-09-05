package hackathon.kb.chakchak.domain.product.repository;

import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("""
        SELECT p FROM Product p
        LEFT JOIN FETCH p.seller
        WHERE p.id = :id
    """)
	Optional<Product> findByIdWithSeller(@Param("id") Long id);

	@EntityGraph(attributePaths = {"seller"})
	@Query("""
        SELECT p FROM Product p
        WHERE (:category IS NULL OR p.category = :category)
          AND (:status   IS NULL OR p.status   = :status)
          AND (:isCoupon IS NULL OR p.isCoupon = :isCoupon)
        """)
	Page<Product> findByOptions(
			@Param("category") Category category,
			@Param("status") ProductStatus status,
			@Param("isCoupon") Boolean isCoupon,
			Pageable pageable
	);
         
	Optional<Product> findById(long id);

	List<Product> findBySeller(Seller seller);
}
