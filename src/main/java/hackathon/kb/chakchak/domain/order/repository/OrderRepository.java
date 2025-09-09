package hackathon.kb.chakchak.domain.order.repository;

import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findById(Long id);
  
	List<Order> findByBuyer(Buyer buyer);
	List<Order> findByBuyer_Id(Long buyerId);


	@Query("""
		SELECT o
		FROM Order o
		JOIN FETCH o.product p
		WHERE o.buyer.id = :buyerId
		  AND p.status IN (
            hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus.SUCCESS,
            hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus.PENDING
          )
		ORDER BY o.createdAt DESC
	""")
	List<Order> findOrdersWithProductByBuyer(@Param("buyerId") Long buyerId);
}
