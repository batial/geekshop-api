package batial.geekshop.api.dto.response;

import batial.geekshop.api.model.ProductVariant;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantResponse {

    private String id;
    private String size;
    private String color;
    private Integer stock;
    private BigDecimal priceModifier;
    private BigDecimal finalPrice;

    public static ProductVariantResponse fromVariant(ProductVariant variant) {
        ProductVariantResponse response = new ProductVariantResponse();
        response.setId(variant.getId().toString());
        response.setSize(variant.getSize());
        response.setColor(variant.getColor());
        response.setStock(variant.getStock());
        response.setPriceModifier(variant.getPriceModifier());
        response.setFinalPrice(variant.getFinalPrice());
        return response;
    }
}