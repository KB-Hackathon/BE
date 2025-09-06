package hackathon.kb.chakchak.domain.order.repository;

import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findById(Long id);

	List<Order> findByBuyer_Id(Long buyerId);
}
