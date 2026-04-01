package batial.geekshop.api.service;

import batial.geekshop.api.exception.ApiException;
import batial.geekshop.api.model.Category;
import batial.geekshop.api.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void findAll_shouldReturnAllCategories() {
        Category cat1 = Category.builder().name("Remeras").slug("remeras").build();
        Category cat2 = Category.builder().name("Figuras").slug("figuras").build();

        when(categoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

        List<Category> result = categoryService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Remeras");
    }

    @Test
    void findById_shouldReturnCategory_whenExists() {
        UUID id = UUID.randomUUID();
        Category category = Category.builder().name("Remeras").slug("remeras").build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        Category result = categoryService.findById(id);

        assertThat(result.getName()).isEqualTo("Remeras");
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(id))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void create_shouldCreateCategory_whenNameIsNotTaken() {
        when(categoryRepository.existsByName("Remeras")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.create("Remeras", "Remeras anime y gamer");

        assertThat(result.getName()).isEqualTo("Remeras");
        assertThat(result.getSlug()).isEqualTo("remeras");
        assertThat(result.getDescription()).isEqualTo("Remeras anime y gamer");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void create_shouldThrowException_whenNameAlreadyExists() {
        when(categoryRepository.existsByName("Remeras")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create("Remeras", "desc"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Category already exists");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void update_shouldUpdateCategory_whenNameIsAvailable() {
        UUID id = UUID.randomUUID();
        Category existing = Category.builder()
                .name("Remeras")
                .slug("remeras")
                .description("desc vieja")
                .build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName("Figuras")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.update(id, "Figuras", "desc nueva");

        assertThat(result.getName()).isEqualTo("Figuras");
        assertThat(result.getSlug()).isEqualTo("figuras");
        assertThat(result.getDescription()).isEqualTo("desc nueva");
    }

    @Test
    void update_shouldNotThrow_whenNameDidNotChange() {
        UUID id = UUID.randomUUID();
        Category existing = Category.builder()
                .name("Remeras")
                .slug("remeras")
                .description("desc vieja")
                .build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThatNoException().isThrownBy(() ->
                categoryService.update(id, "Remeras", "desc nueva"));
    }

    @Test
    void update_shouldThrowException_whenNewNameAlreadyExists() {
        UUID id = UUID.randomUUID();
        Category existing = Category.builder()
                .name("Remeras")
                .slug("remeras")
                .build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName("Figuras")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.update(id, "Figuras", "desc"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Category name already exists");
    }
}