package com.spartaifive.commercepayment.domain.payment.service;

import com.spartaifive.commercepayment.common.external.portone.PortOneClient;
import com.spartaifive.commercepayment.common.external.portone.PortOnePaymentResponse;
import com.spartaifive.commercepayment.domain.order.entity.Order;
import com.spartaifive.commercepayment.domain.order.repository.OrderRepository;
import com.spartaifive.commercepayment.domain.payment.dto.ConfirmPaymentRequest;
import com.spartaifive.commercepayment.domain.payment.dto.ConfirmPaymentResponse;
import com.spartaifive.commercepayment.domain.payment.dto.PaymentAttemptRequest;
import com.spartaifive.commercepayment.domain.payment.dto.PaymentAttemptResponse;
import com.spartaifive.commercepayment.domain.payment.entity.Payment;
import com.spartaifive.commercepayment.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PortOneClient portOneClient;

    /**
     * 결제 시도(Attempt) 생성
     * - Order 조회
     * - expectedAmount 결정(= 주문 총액)
     * - Payment.createAttempt로 엔티티 생성
     * - 저장 후 Response DTO 반환
     */
    @Transactional
    public PaymentAttemptResponse createPayment(Long userId, PaymentAttemptRequest request) {
        Order order = orderRepository.findById(request.orderId()).orElseThrow(
                () -> new IllegalArgumentException("주문이 존재하지 않습니다 orderId=" + request.orderId())
        ); // NotFoundException 예외 처리
        BigDecimal expectedAmount = order.getTotalPrice();

        // merchantId 생성
        String merchantPaymentId = "pay_" + UUID.randomUUID();

        Payment payment = Payment.createAttempt(userId, order, expectedAmount, merchantPaymentId);
        Payment savedPayment = paymentRepository.save(payment);

        return PaymentAttemptResponse.from(savedPayment);
    }

    /**
     * 결제 확정(Confirm)
     * - Payment 조회 (merchantPaymentId 또는 paymentId 기준)
     * - 이미 확정/실패된 결제인지 상태 검증 => 결제 상태가 READY인지 확인 (멱등 처리)
     * - PortOne 결제 결과 조회 및 검증
     *   · 결제 상태(PAID 여부)
     *   · 결제 금액 검증 (actualAmount == expectedAmount)
     * - PortOne 결제 식별자(portonePaymentId) 저장 (UNIQUE, 멱등성 보장)
     * - 결제 상태를 PAID로 전이
     * - Order 조회
     * - 재고 수량 검증
     * - 재고 차감
     * - 결제 확정 결과 Response DTO 반환
     */
    @Transactional
    public ConfirmPaymentResponse confirmPayment(Long userId, ConfirmPaymentRequest request) {
        // 외부 portonePaymentId, orderId null값 검증
        if (request.portonePaymentId() == null || request.portonePaymentId().isBlank()) {
            throw new IllegalArgumentException("portonePaymentId는 필수 입니다");
        }
        if (request.orderId() == null) {
            throw new IllegalArgumentException("orderId는 필수 입니다");
        }

        // portonePaymentId로 이미 처리된 결제면 그대로 반환 (멱등 처리)
        Optional<Payment> exOpt = paymentRepository.findByPortonePaymentId(request.portonePaymentId());
        if (exOpt.isPresent()) {
            Payment existing = exOpt.get();
            if (!existing.getUserId().equals(userId)) {
                throw new IllegalStateException("결제 소유자가 아닙니다");
            }
            return ConfirmPaymentResponse.from(existing);
        }


//        if (payment.getPortonePaymentId() != null) {
//            PortOnePaymentResponse portOneResponse = portOneClient.getPayment(payment.getPortonePaymentId());
//            if (!portOneResponse.isPaid()) { // 결제 확정이 아니면 실패
//                Payment failedPayment = payment.fail();
//                paymentRepository.save(failedPayment);
//                orderRepository.findById(failedPayment.getOrder().getId()).orElseThrow(
//                        () ->
//                )
//            }
//        }
    }

}
