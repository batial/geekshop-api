package batial.geekshop.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItemRequest {

    @NotNull(message = "Product ID is required")
    private String productId;

    private String variantId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public String getProductId() { return productId; }
    public String getVariantId() { return variantId; }
    public Integer getQuantity() { return quantity; }

    public void setProductId(String productId) { this.productId = productId; }
    public void setVariantId(String variantId) { this.variantId = variantId; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}