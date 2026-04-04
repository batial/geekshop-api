package batial.geekshop.api.controller;

import batial.geekshop.api.dto.request.ProductRequest;
import batial.geekshop.api.dto.response.ProductResponse;
import batial.geekshop.api.model.Product;
import batial.geekshop.api.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type) {

        Product.ProductType productType = null;
        if (type != null) {
            productType = Product.ProductType.valueOf(type.toUpperCase());
        }

        return ResponseEntity.ok(
                productService.findAll(page, size, sortBy, search, productType).map(ProductResponse::new)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> findByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.findByCategory(categoryId, page, size).map(ProductResponse::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(new ProductResponse(productService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(new ProductResponse(productService.create(
                request.getName(), request.getDescription(), request.getPrice(),
                request.getStock(), Product.ProductType.valueOf(request.getType()),
                request.getCategoryId())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(new ProductResponse(productService.update(
                id, request.getName(), request.getDescription(), request.getPrice(),
                request.getStock(), request.getCategoryId())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    @GetMapping("/category/slug/{slug}")
    public ResponseEntity<Page<ProductResponse>> findByCategorySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                productService.findByCategorySlug(slug, page, size).map(ProductResponse::new)
        );
    }
}