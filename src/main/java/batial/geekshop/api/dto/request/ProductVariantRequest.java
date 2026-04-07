package batial.geekshop.api.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantRequest {

    private String size;

    private String color;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock = 0;

    private BigDecimal priceModifier = BigDecimal.ZERO;
}