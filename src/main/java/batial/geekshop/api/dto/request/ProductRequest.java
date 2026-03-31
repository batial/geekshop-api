package batial.geekshop.api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String type;
    private UUID categoryId;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getStock() { return stock; }
    public String getType() { return type; }
    public UUID getCategoryId() { return categoryId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStock(Integer stock) { this.stock = stock; }
    public void setType(String type) { this.type = type; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
}