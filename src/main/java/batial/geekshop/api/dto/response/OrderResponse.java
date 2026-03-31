package batial.geekshop.api.dto.response;

import batial.geekshop.api.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderResponse {
    private UUID id;
    private String status;
    private BigDecimal total;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.status = order.getStatus().name();
        this.total = order.getTotal();
        this.shippingAddress = order.getShippingAddress();
        this.createdAt = order.getCreatedAt();
        this.items = order.getItems().stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
    }

    public UUID getId() { return id; }
    public String getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public String getShippingAddress() { return shippingAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<OrderItemResponse> getItems() { return items; }
}