package batial.geekshop.api.dto.response;

import batial.geekshop.api.model.OrderItem;
import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemResponse {
    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String size;
    private String color;

    public OrderItemResponse(OrderItem item) {
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
        this.subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        this.size = item.getSize();
        this.color = item.getColor();
    }

    public UUID getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getSubtotal() { return subtotal; }
    public String getSize() { return size; }
    public String getColor() { return color; }
}