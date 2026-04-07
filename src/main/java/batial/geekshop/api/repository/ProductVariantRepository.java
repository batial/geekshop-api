package batial.geekshop.api.repository;

import batial.geekshop.api.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    List<ProductVariant> findByProductId(UUID productId);
    Optional<ProductVariant> findByProductIdAndSizeAndColor(UUID productId, String size, String color);
}