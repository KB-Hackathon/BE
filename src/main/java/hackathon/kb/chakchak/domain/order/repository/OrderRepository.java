package hackathon.kb.chakchak.domain.order.repository;

import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findById(Long id);

	List<Order> findByBuyer_Id(Long buyerId);

	@Query("""
        select o
        from Order o
        join fetch o.product p
        where o.buyer.id = :buyerId
        order by o.createdAt desc
    """)
	List<Order> findAllWithProductByBuyer(@Param("buyerId") Long buyerId);
}
