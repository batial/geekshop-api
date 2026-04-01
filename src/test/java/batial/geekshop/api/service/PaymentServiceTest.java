package batial.geekshop.api.service;

import batial.geekshop.api.exception.ApiException;
import batial.geekshop.api.model.Order;
import batial.geekshop.api.model.Payment;
import batial.geekshop.api.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentService paymentService;

    private Order buildOrder() {
        return Order.builder()
                .status(Order.OrderStatus.PENDING)
                .total(new BigDecimal("59.98"))
                .shippingAddress("Calle 123")
                .build();
    }

    private Payment buildPayment(Order order) {
        return Payment.builder()
                .order(order)
                .amount(new BigDecimal("59.98"))
                .status(Payment.PaymentStatus.PENDING)
                .build();
    }

    @Test
    void create_shouldCreatePayment_withCorrectAmount() {
        UUID orderId = UUID.randomUUID();
        Order order = buildOrder();

        when(orderService.findById(orderId)).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.create(orderId);

        assertThat(result.getAmount()).isEqualByComparingTo("59.98");
        assertThat(result.getStatus()).isEqualTo(Payment.PaymentStatus.PENDING);
        assertThat(result.getOrder()).isEqualTo(order);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void updateFromWebhook_shouldApprovePayment_andConfirmOrder() {
        Order order = buildOrder();
        Payment payment = buildPayment(order);
        payment.setMpReference("mp-ref-123");

        when(paymentRepository.findByMpReference("mp-ref-123")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.updateFromWebhook("mp-ref-123", Payment.PaymentStatus.APPROVED);

        assertThat(result.getStatus()).isEqualTo(Payment.PaymentStatus.APPROVED);
        assertThat(result.getPaidAt()).isNotNull();
        verify(orderService, times(1)).updateStatus(any(), eq(Order.OrderStatus.CONFIRMED));
    }

    @Test
    void updateFromWebhook_shouldRejectPayment_andCancelOrder() {
        Order order = buildOrder();
        Payment payment = buildPayment(order);
        payment.setMpReference("mp-ref-123");

        when(paymentRepository.findByMpReference("mp-ref-123")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.updateFromWebhook("mp-ref-123", Payment.PaymentStatus.REJECTED);

        assertThat(result.getStatus()).isEqualTo(Payment.PaymentStatus.REJECTED);
        assertThat(result.getPaidAt()).isNull();
        verify(orderService, times(1)).updateStatus(any(), eq(Order.OrderStatus.CANCELLED));
    }

    @Test
    void updateFromWebhook_shouldThrowException_whenReferenceNotFound() {
        when(paymentRepository.findByMpReference("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.updateFromWebhook("inexistente", Payment.PaymentStatus.APPROVED))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Payment not found");
    }

    @Test
    void findByOrder_shouldReturnPayment_whenExists() {
        UUID orderId = UUID.randomUUID();
        Order order = buildOrder();
        Payment payment = buildPayment(order);

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        Payment result = paymentService.findByOrder(orderId);

        assertThat(result.getAmount()).isEqualByComparingTo("59.98");
        assertThat(result.getStatus()).isEqualTo(Payment.PaymentStatus.PENDING);
    }

    @Test
    void findByOrder_shouldThrowException_whenNotFound() {
        UUID orderId = UUID.randomUUID();
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.findByOrder(orderId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Payment not found");
    }
}