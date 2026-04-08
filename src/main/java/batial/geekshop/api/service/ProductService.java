package batial.geekshop.api.service;

import batial.geekshop.api.exception.ApiException;
import batial.geekshop.api.model.Product;
import batial.geekshop.api.model.Category;
import batial.geekshop.api.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import batial.geekshop.api.dto.request.ProductVariantRequest;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductVariantService variantService;

    public Page<Product> findAll(int page, int size, String sortBy, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return productRepository.findByFilters(search, pageable);
    }

    public Page<Product> findByCategory(UUID categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findByActiveTrueAndCategoryId(categoryId, pageable);
    }

    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Product create(String name, String description, BigDecimal price,
                          Integer stock, UUID categoryId,
                          List<ProductVariantRequest> variants) {

        Category category = categoryService.findById(categoryId);

        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .category(category)
                .active(true)
                .build();

        Product savedProduct = productRepository.save(product);

        if (category.getHasVariants() && variants != null && !variants.isEmpty()) {
            for (ProductVariantRequest variantRequest : variants) {
                variantService.createVariant(
                        savedProduct,
                        variantRequest.getSize(),
                        variantRequest.getColor(),
                        variantRequest.getStock(),
                        variantRequest.getPriceModifier()
                );
            }
        }

        return savedProduct;
    }

    public Product update(UUID id, String name, String description,
                          BigDecimal price, Integer stock, UUID categoryId) {

        Product product = findById(id);
        Category category = categoryService.findById(categoryId);

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);

        return productRepository.save(product);
    }

    public void delete(UUID id) {
        Product product = findById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    public Product updateStock(UUID id, int quantity) {
        Product product = findById(id);

        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            throw new ApiException("Insufficient stock for product: " + product.getName(), HttpStatus.BAD_REQUEST);
        }

        product.setStock(newStock);
        return productRepository.save(product);
    }

    public Page<Product> findByCategorySlug(String slug, int page, int size) {
        Category category = categoryService.findBySlug(slug);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findByActiveTrueAndCategoryId(category.getId(), pageable);
    }
}