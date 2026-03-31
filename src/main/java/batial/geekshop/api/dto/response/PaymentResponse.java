package batial.geekshop.api.dto.response;

import batial.geekshop.api.model.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentResponse {
    private UUID id;
    private String status;
    private BigDecimal amount;
    private String mpReference;
    private LocalDateTime paidAt;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.status = payment.getStatus().name();
        this.amount = payment.getAmount();
        this.mpReference = payment.getMpReference();
        this.paidAt = payment.getPaidAt();
    }

    public UUID getId() { return id; }
    public String getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public String getMpReference() { return mpReference; }
    public LocalDateTime getPaidAt() { return paidAt; }
}