package batial.geekshop.api.service;

import batial.geekshop.api.exception.ApiException;
import batial.geekshop.api.model.Category;
import batial.geekshop.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException("Category not found", HttpStatus.NOT_FOUND));
    }

    public Category findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ApiException("Category not found", HttpStatus.NOT_FOUND));
    }

    public Category create(String name, String description, Boolean hasVariants) {
        if (categoryRepository.existsByName(name)) {
            throw new ApiException("Category already exists", HttpStatus.CONFLICT);
        }

        Category category = Category.builder()
                .name(name)
                .slug(generateSlug(name))
                .description(description)
                .hasVariants(hasVariants != null ? hasVariants : false)
                .build();

        return categoryRepository.save(category);
    }

    public Category update(UUID id, String name, String description, Boolean hasVariants) {
        Category category = findById(id);

        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new ApiException("Category name already exists", HttpStatus.CONFLICT);
        }

        category.setName(name);
        category.setSlug(generateSlug(name));
        category.setDescription(description);
        category.setHasVariants(hasVariants != null ? hasVariants : false);

        return categoryRepository.save(category);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}