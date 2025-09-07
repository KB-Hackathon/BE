package hackathon.kb.chakchak.domain.order.repository;

import hackathon.kb.chakchak.domain.order.api.dto.res.CouponItemDto;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hackathon.kb.chakchak.domain.order.domain.entity.Coupon;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

	Optional<Coupon> findById(Long id);

	Optional<Coupon> findByUuid(String uuid);

	@Query("""
		SELECT new hackathon.kb.chakchak.domain.order.api.dto.res.CouponItemDto(
			c.id, c.uuid, p.couponName, c.expiration, c.isUsed,
			o.id, o.buyer.id, p.id, p.title, s.id, s.companyName
		)
		FROM Coupon c
		JOIN c.order o
		JOIN o.product p
		JOIN p.seller s
		WHERE o.buyer.id = :memberId
		ORDER BY
		CASE WHEN c.expiration IS NULL THEN 1 ELSE 0 END ASC, c.expiration ASC, o.createdAt DESC
	""")
	List<CouponItemDto> findMyCoupons(@Param("memberId") Long memberId);

	@Query("""
		SELECT new hackathon.kb.chakchak.domain.order.api.dto.res.CouponItemDto(
			c.id, c.uuid, p.couponName, c.expiration, c.isUsed,
			o.id, o.buyer.id, p.id, p.title, s.id, s.companyName
		)
		FROM Coupon c
		JOIN c.order o
		JOIN o.product p
		JOIN p.seller s
		WHERE c.id = :couponId
	""")
	CouponItemDto findCoupon(@Param("couponId") Long couponId);

	@Query("""
		SELECT new hackathon.kb.chakchak.domain.order.api.dto.res.CouponItemDto(
			c.id, c.uuid, p.couponName, c.expiration, c.isUsed,
			o.id, o.buyer.id, p.id, p.title, s.id, s.companyName
		)
		FROM Coupon c
		JOIN c.order o
		JOIN o.product p
		JOIN p.seller s
		WHERE c.uuid = :uuid
	""")
	Optional<CouponItemDto> findBuyerCouponByUUID(@Param("uuid") String uuid);

}
