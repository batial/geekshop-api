package batial.geekshop.api.service;

import batial.geekshop.api.model.Category;
import batial.geekshop.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Category findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Category create(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("Category already exists");
        }

        Category category = Category.builder()
                .name(name)
                .slug(generateSlug(name))
                .description(description)
                .build();

        return categoryRepository.save(category);
    }

    public Category update(UUID id, String name, String description) {
        Category category = findById(id);

        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new RuntimeException("Category name already exists");
        }

        category.setName(name);
        category.setSlug(generateSlug(name));
        category.setDescription(description);

        return categoryRepository.save(category);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}