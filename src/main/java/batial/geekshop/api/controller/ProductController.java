package batial.geekshop.api.controller;

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
    public ResponseEntity<Page<Product>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        return ResponseEntity.ok(productService.findAll(page, size, sortBy));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> findByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.findByCategory(categoryId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> create(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(productService.create(
                (String) body.get("name"),
                (String) body.get("description"),
                new BigDecimal(body.get("price").toString()),
                (Integer) body.get("stock"),
                Product.ProductType.valueOf((String) body.get("type")),
                UUID.fromString((String) body.get("categoryId"))
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> update(@PathVariable UUID id,
                                          @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(productService.update(
                id,
                (String) body.get("name"),
                (String) body.get("description"),
                new BigDecimal(body.get("price").toString()),
                (Integer) body.get("stock"),
                UUID.fromString((String) body.get("categoryId"))
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }
}