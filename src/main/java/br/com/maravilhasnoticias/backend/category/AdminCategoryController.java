package br.com.maravilhasnoticias.backend.category;

import br.com.maravilhasnoticias.backend.category.dto.CategoryRequest;
import br.com.maravilhasnoticias.backend.category.dto.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;
    public AdminCategoryController(CategoryService categoryService) { this.categoryService = categoryService; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) { return categoryService.create(request); }
    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) { return categoryService.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { categoryService.delete(id); }
}
