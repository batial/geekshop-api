package batial.geekshop.api.service;

import batial.geekshop.api.model.Product;
import batial.geekshop.api.model.ProductImage;
import batial.geekshop.api.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductService productService;

    @Transactional
    public ProductImage addImage(UUID productId, String url, boolean isMain) {
        Product product = productService.findById(productId);

        if (isMain) {
            productImageRepository.findByProductId(productId)
                    .forEach(img -> {
                        img.setIsMain(false);
                        productImageRepository.save(img);
                    });
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .url(url)
                .isMain(isMain)
                .build();

        return productImageRepository.save(image);
    }

    public List<ProductImage> findByProduct(UUID productId) {
        return productImageRepository.findByProductId(productId);
    }

    @Transactional
    public void deleteImage(UUID imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        productImageRepository.delete(image);
    }
}