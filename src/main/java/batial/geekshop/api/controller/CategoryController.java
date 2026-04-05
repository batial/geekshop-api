package batial.geekshop.api.controller;

import batial.geekshop.api.dto.request.CategoryRequest;
import batial.geekshop.api.dto.response.CategoryResponse;
import batial.geekshop.api.model.Category;
import batial.geekshop.api.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {
        return ResponseEntity.ok(categoryService.findAll().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(new CategoryResponse(categoryService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(new CategoryResponse(
                categoryService.create(request.getName(), request.getDescription())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> update(@Valid @PathVariable UUID id, @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(new CategoryResponse(
                categoryService.update(id, request.getName(), request.getDescription())));
    }
}