package hackathon.kb.chakchak.domain.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hackathon.kb.chakchak.domain.order.domain.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

	Optional<Coupon> findById(Long id);

}
