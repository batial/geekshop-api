package batial.geekshop.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "size")
    private String size;  // "S", "M", "L", "XL", "XXL" (solo para remeras)

    @Column(name = "color")
    private String color;  // "Negro", "Blanco", "Rojo", etc. (solo para remeras)

    @Builder.Default
    @Column(nullable = false)
    private Integer stock = 0;

    @Builder.Default
    @Column(name = "price_modifier", precision = 10, scale = 2)
    private BigDecimal priceModifier = BigDecimal.ZERO;

    public BigDecimal getFinalPrice() {
        if (product == null) {
            return priceModifier;
        }
        return product.getPrice().add(priceModifier);
    }
}