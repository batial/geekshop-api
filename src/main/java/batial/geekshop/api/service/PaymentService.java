package batial.geekshop.api.service;

import batial.geekshop.api.exception.ApiException;
import batial.geekshop.api.model.Order;
import batial.geekshop.api.model.Payment;
import batial.geekshop.api.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Transactional
    public Payment create(UUID orderId) {
        Order order = orderService.findById(orderId);

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotal())
                .status(Payment.PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment updateFromWebhook(String mpReference, Payment.PaymentStatus status) {
        Payment payment = paymentRepository.findByMpReference(mpReference)
                .orElseThrow(() -> new ApiException("Payment not found", HttpStatus.NOT_FOUND));

        payment.setStatus(status);

        if (status == Payment.PaymentStatus.APPROVED) {
            payment.setPaidAt(LocalDateTime.now());
            orderService.updateStatus(
                    payment.getOrder().getId(),
                    Order.OrderStatus.CONFIRMED
            );
        }

        if (status == Payment.PaymentStatus.REJECTED) {
            orderService.updateStatus(
                    payment.getOrder().getId(),
                    Order.OrderStatus.CANCELLED
            );
        }

        return paymentRepository.save(payment);
    }

    public Payment findByOrder(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiException("Payment not found", HttpStatus.NOT_FOUND));
    }

    public void assignMpReference(UUID paymentId, String mpReference) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ApiException("Payment not found", HttpStatus.NOT_FOUND));
        payment.setMpReference(mpReference);
        paymentRepository.save(payment);
    }
}