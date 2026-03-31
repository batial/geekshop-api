package batial.geekshop.api.controller;

import batial.geekshop.api.dto.response.PaymentResponse;
import batial.geekshop.api.model.Payment;
import batial.geekshop.api.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(new PaymentResponse(
                paymentService.create(UUID.fromString(body.get("orderId")))));
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody Map<String, Object> body) {
        String mpReference = (String) body.get("data.id");
        String status = (String) body.get("type");

        if ("payment".equals(status)) {
            paymentService.updateFromWebhook(
                    mpReference,
                    Payment.PaymentStatus.APPROVED
            );
        }

        return ResponseEntity.ok(Map.of("received", true));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> findByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(new PaymentResponse(paymentService.findByOrder(orderId)));
    }
}