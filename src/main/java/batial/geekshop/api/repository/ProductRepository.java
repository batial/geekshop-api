package batial.geekshop.api.repository;

import batial.geekshop.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByActiveTrue(Pageable pageable);
    Page<Product> findByActiveTrueAndCategoryId(UUID categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true " +
            "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
            "AND (:type IS NULL OR p.type = :type)")
    Page<Product> findByFilters(@Param("search") String search,
                                @Param("type") Product.ProductType type,
                                Pageable pageable);
}