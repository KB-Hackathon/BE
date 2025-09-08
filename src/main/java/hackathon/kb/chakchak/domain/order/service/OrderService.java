package hackathon.kb.chakchak.domain.order.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hackathon.kb.chakchak.domain.escrow.service.EscrowService;
import hackathon.kb.chakchak.domain.ledger.entity.TransactionType;
import hackathon.kb.chakchak.domain.ledger.service.LedgerService;
import hackathon.kb.chakchak.domain.member.domain.entity.Buyer;
import hackathon.kb.chakchak.domain.member.repository.BuyerRepository;
import hackathon.kb.chakchak.domain.order.api.dto.req.CouponOrderReq;
import hackathon.kb.chakchak.domain.order.api.dto.res.CouponOrderRes;
import hackathon.kb.chakchak.domain.order.domain.entity.Coupon;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.order.domain.enums.OrderStatus;
import hackathon.kb.chakchak.domain.order.repository.CouponRepository;
import hackathon.kb.chakchak.domain.order.repository.OrderRepository;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final BuyerRepository buyerRepository;
	private final ProductRepository productRepository;
	private final CouponRepository couponRepository;
	private final OrderRepository orderRepository;
	private final LedgerService ledgerService;
	private final EscrowService escrowService;

	/**
	 *
	 * @param memberId
	 * @param couponOrderReq : 구매할 쿠푠 개수
	 * @return
	 */
	@Transactional
	public CouponOrderRes orderCoupon(Long memberId, CouponOrderReq couponOrderReq) {

		log.info("memberId = {}", memberId);
		Buyer buyer = buyerRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ResponseCode.MEMBER_NOT_FOUND));

		Product product = productRepository.findById(couponOrderReq.getProductId())
			.orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));

		String transactionId = UUID.randomUUID().toString().replace("-", "");

		BigDecimal quantity = BigDecimal.valueOf(couponOrderReq.getQuantity());
		BigDecimal totalPrice = product.getPrice().multiply(quantity);

		Order order = Order.builder()
			.product(product)
			.buyer(buyer)
			.quantity(couponOrderReq.getQuantity())
			.status(OrderStatus.PAY_COMPLETE)
			.price(totalPrice)
			.transactionId(transactionId)
			.build();

		Coupon coupon = Coupon.builder()
			.order(order)
			.uuid(UUID.randomUUID().toString().replace("-", ""))
			.expiration(LocalDate.now().plusYears(1L))
			.build();

		orderRepository.save(order);
		couponRepository.save(coupon);

		ledgerService.createAndSaveVoucherWithDoubleEntry(transactionId, TransactionType.TRANSFER, totalPrice);

		return new CouponOrderRes(coupon.getUuid(), coupon.getExpiration());
	}

	@Transactional
	public void terminateCoupon(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));

		for (Order order : product.getOrders()) {
			order.getCoupon().activeCoupon();
		}

		escrowService.terminateBuy(product);
	}
}
