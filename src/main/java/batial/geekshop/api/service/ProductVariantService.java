package batial.geekshop.api.service;

import batial.geekshop.api.exception.ApiException;
import batial.geekshop.api.model.Product;
import batial.geekshop.api.model.ProductVariant;
import batial.geekshop.api.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductVariantRepository variantRepository;

    @Transactional
    public ProductVariant createVariant(Product product, String size, String color, Integer stock, BigDecimal priceModifier) {
        variantRepository.findByProductIdAndSizeAndColor(product.getId(), size, color)
                .ifPresent(v -> {
                    throw new ApiException("Ya existe una variante con este talle y color", HttpStatus.BAD_REQUEST);
                });

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSize(size);
        variant.setColor(color);
        variant.setStock(stock != null ? stock : 0);
        variant.setPriceModifier(priceModifier != null ? priceModifier : BigDecimal.ZERO);

        return variantRepository.save(variant);
    }

    public List<ProductVariant> getVariantsByProduct(UUID productId) {
        return variantRepository.findByProductId(productId);
    }

    public ProductVariant getVariantById(UUID variantId) {
        return variantRepository.findById(variantId)
                .orElseThrow(() -> new ApiException("Variante no encontrada", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public ProductVariant updateVariantStock(UUID variantId, Integer stock) {
        ProductVariant variant = getVariantById(variantId);
        variant.setStock(stock);
        return variantRepository.save(variant);
    }

    @Transactional
    public void deleteVariant(UUID variantId) {
        ProductVariant variant = getVariantById(variantId);
        variantRepository.delete(variant);
    }

    @Transactional
    public void decreaseStock(UUID variantId, Integer quantity) {
        ProductVariant variant = getVariantById(variantId);

        if (variant.getStock() < quantity) {
            throw new ApiException("Stock insuficiente para esta variante", HttpStatus.BAD_REQUEST);
        }

        variant.setStock(variant.getStock() - quantity);
        variantRepository.save(variant);
    }
}