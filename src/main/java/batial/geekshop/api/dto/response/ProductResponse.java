package batial.geekshop.api.dto.response;

import batial.geekshop.api.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Boolean active;
    private UUID categoryId;
    private String categoryName;
    private List<String> imageUrls;
    private String mainImageUrl;
    private List<ProductVariantResponse> variants;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.active = product.getActive();
        this.categoryId = product.getCategory().getId();
        this.categoryName = product.getCategory().getName();
        this.imageUrls = product.getImages().stream()
                .map(img -> img.getUrl())
                .collect(Collectors.toList());
        this.mainImageUrl = product.getImages().stream()
                .filter(img -> img.getIsMain())
                .map(img -> img.getUrl())
                .findFirst()
                .orElse(null);
        this.variants = product.getVariants().stream()
                        .map(ProductVariantResponse::fromVariant)
                        .collect(Collectors.toList());
    }

    public static ProductResponse fromProduct(Product product) {
        return new ProductResponse(product);
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getStock() { return stock; }
    public Boolean getActive() { return active; }
    public UUID getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public List<String> getImageUrls() { return imageUrls; }
    public String getMainImageUrl() { return mainImageUrl; }
    public List<ProductVariantResponse> getVariants() { return variants; }
}