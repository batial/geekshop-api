package batial.geekshop.api.service;

import batial.geekshop.api.dto.request.ProductVariantRequest;
import batial.geekshop.api.exception.ApiException;
import batial.geekshop.api.model.Category;
import batial.geekshop.api.model.Product;
import batial.geekshop.api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductVariantService variantService;

    @InjectMocks
    private ProductService productService;

    private Category buildCategory() {
        return Category.builder()
                .name("Remeras")
                .slug("remeras")
                .hasVariants(true)
                .build();
    }

    private Product buildProduct(Category category) {
        return Product.builder()
                .name("Remera Naruto")
                .description("Remera negra")
                .price(new BigDecimal("29.99"))
                .stock(50)
                .category(category)
                .active(true)
                .build();
    }

    @Test
    void findAll_shouldReturnPageOfProducts() {
        Product product = buildProduct(buildCategory());
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findByFilters(isNull(), any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.findAll(0, 20, "createdAt", null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Remera Naruto");
    }

    @Test
    void findById_shouldReturnProduct_whenExists() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(buildCategory());

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        Product result = productService.findById(id);

        assertThat(result.getName()).isEqualTo("Remera Naruto");
        assertThat(result.getPrice()).isEqualByComparingTo("29.99");
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(id))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void create_shouldCreateProduct_withCorrectFields() {
        Category category = buildCategory();
        UUID categoryId = UUID.randomUUID();

        when(categoryService.findById(categoryId)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.create(
                "Remera Naruto", "Remera negra",
                new BigDecimal("29.99"), 50, categoryId,
                null);

        assertThat(result.getName()).isEqualTo("Remera Naruto");
        assertThat(result.getPrice()).isEqualByComparingTo("29.99");
        assertThat(result.getStock()).isEqualTo(50);
        assertThat(result.getCategory().getName()).isEqualTo("Remeras");
        assertThat(result.getActive()).isTrue();
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void create_shouldCreateProductWithVariants_whenTypeIsShirt() {
        Category category = buildCategory();
        UUID categoryId = UUID.randomUUID();

        List<ProductVariantRequest> variants = Arrays.asList(
                createVariantRequest("M", "Negro", 10, BigDecimal.ZERO),
                createVariantRequest("L", "Blanco", 5, BigDecimal.ZERO)
        );

        when(categoryService.findById(categoryId)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.create(
                "Remera Naruto", "Remera negra",
                new BigDecimal("29.99"), 0,  // stock 0 porque está en variantes
                categoryId,
                variants);

        assertThat(result.getName()).isEqualTo("Remera Naruto");
        verify(variantService, times(2)).createVariant(any(), anyString(), anyString(), anyInt(), any());
    }

    private ProductVariantRequest createVariantRequest(String size, String color, int stock, BigDecimal priceModifier) {
        ProductVariantRequest request = new ProductVariantRequest();
        request.setSize(size);
        request.setColor(color);
        request.setStock(stock);
        request.setPriceModifier(priceModifier);
        return request;
    }

    @Test
    void delete_shouldSetActiveToFalse() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(buildCategory());

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        productService.delete(id);

        assertThat(product.getActive()).isFalse();
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateStock_shouldReduceStock_whenSufficient() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(buildCategory());

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateStock(id, 10);

        assertThat(result.getStock()).isEqualTo(40);
    }

    @Test
    void updateStock_shouldThrowException_whenInsufficientStock() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(buildCategory());

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateStock(id, 100))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Insufficient stock");
    }
}