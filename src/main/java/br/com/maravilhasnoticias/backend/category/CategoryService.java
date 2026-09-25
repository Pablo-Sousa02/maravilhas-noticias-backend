package br.com.maravilhasnoticias.backend.category;

import br.com.maravilhasnoticias.backend.category.dto.CategoryRequest;
import br.com.maravilhasnoticias.backend.category.dto.CategoryResponse;
import br.com.maravilhasnoticias.backend.common.exception.ConflictException;
import br.com.maravilhasnoticias.backend.common.exception.ResourceNotFoundException;
import br.com.maravilhasnoticias.backend.common.util.SlugUtils;
import br.com.maravilhasnoticias.backend.news.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final NewsRepository newsRepository;

    public CategoryService(CategoryRepository categoryRepository, NewsRepository newsRepository) {
        this.categoryRepository = categoryRepository;
        this.newsRepository = newsRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> listPublic() {
        return categoryRepository.findAllByActiveTrueOrderByNameAsc().stream().map(CategoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getPublic(String slug) {
        return CategoryResponse.from(categoryRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada")));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String slug = SlugUtils.from(request.name());
        if (categoryRepository.existsBySlug(slug)) throw new ConflictException("Já existe uma categoria com este nome");
        return CategoryResponse.from(categoryRepository.save(new Category(request.name().trim(), slug, request.active())));
    }

    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = find(id);
        String slug = SlugUtils.from(request.name());
        if (categoryRepository.existsBySlugAndIdNot(slug, id)) throw new ConflictException("Já existe uma categoria com este nome");
        category.update(request.name().trim(), slug, request.active());
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(UUID id) {
        Category category = find(id);
        if (newsRepository.existsByCategoryId(id)) throw new ConflictException("A categoria está sendo utilizada por notícias");
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public Category find(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
    }
}
