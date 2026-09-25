package br.com.maravilhasnoticias.backend.news;

import br.com.maravilhasnoticias.backend.category.Category;
import br.com.maravilhasnoticias.backend.category.CategoryService;
import br.com.maravilhasnoticias.backend.common.exception.ConflictException;
import br.com.maravilhasnoticias.backend.common.exception.ResourceNotFoundException;
import br.com.maravilhasnoticias.backend.common.util.SlugUtils;
import br.com.maravilhasnoticias.backend.news.dto.NewsRequest;
import br.com.maravilhasnoticias.backend.news.dto.NewsResponse;
import br.com.maravilhasnoticias.backend.user.User;
import br.com.maravilhasnoticias.backend.user.UserRepository;
import br.com.maravilhasnoticias.backend.push.WebPushService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NewsService {
    private final NewsRepository newsRepository;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final WebPushService webPushService;

    public NewsService(NewsRepository newsRepository, CategoryService categoryService, UserRepository userRepository, WebPushService webPushService) {
        this.newsRepository = newsRepository; this.categoryService = categoryService; this.userRepository = userRepository; this.webPushService = webPushService;
    }

    @Transactional(readOnly = true)
    public Page<NewsResponse> listPublic(String category, Pageable pageable) {
        return newsRepository.findAll(NewsSpecifications.status(NewsStatus.PUBLISHED)
                .and(NewsSpecifications.category(category)), pageable).map(NewsResponse::from);
    }
    @Transactional(readOnly = true)
    public Page<NewsResponse> featured(Pageable pageable) {
        return newsRepository.findAll(NewsSpecifications.status(NewsStatus.PUBLISHED).and(NewsSpecifications.featured()), pageable).map(NewsResponse::from);
    }
    @Transactional(readOnly = true)
    public Page<NewsResponse> search(String q, String category, Pageable pageable) {
        return newsRepository.findAll(NewsSpecifications.status(NewsStatus.PUBLISHED)
                .and(NewsSpecifications.text(q)).and(NewsSpecifications.category(category)), pageable).map(NewsResponse::from);
    }
    @Transactional(readOnly = true)
    public NewsResponse getPublic(String slug) {
        return NewsResponse.from(newsRepository.findBySlugAndStatus(slug, NewsStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada")));
    }
    @Transactional(readOnly = true)
    public Page<NewsResponse> listAdmin(NewsStatus status, String category, Pageable pageable) {
        Specification<News> specification = NewsSpecifications.category(category);
        if (status != null) specification = specification.and(NewsSpecifications.status(status));
        return newsRepository.findAll(specification, pageable).map(NewsResponse::from);
    }
    @Transactional(readOnly = true)
    public NewsResponse getAdmin(UUID id) { return NewsResponse.from(find(id)); }
    @Transactional
    public NewsResponse create(NewsRequest request, Jwt jwt) {
        Category category = categoryService.find(request.categoryId());
        User author = userRepository.findById(UUID.fromString(jwt.getSubject()))
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado"));
        String slug = uniqueSlug(request.title(), null);
        News news = new News(request.title().trim(), slug, request.summary().trim(), request.content().trim(),
                blankToNull(request.coverImageUrl()), request.featured(), request.urgent(), category, author);
        return NewsResponse.from(newsRepository.save(news));
    }
    @Transactional
    public NewsResponse update(UUID id, NewsRequest request) {
        News news = find(id); Category category = categoryService.find(request.categoryId());
        news.update(request.title().trim(), uniqueSlug(request.title(), id), request.summary().trim(), request.content().trim(),
                blankToNull(request.coverImageUrl()), request.featured(), request.urgent(), category);
        return NewsResponse.from(news);
    }
    @Transactional
    public NewsResponse publish(UUID id) {
        News news = find(id);
        boolean firstPublication = news.publish();
        NewsResponse response = NewsResponse.from(news);
        if (firstPublication && news.isUrgent()) webPushService.notifyUrgentNews(news);
        return response;
    }
    @Transactional
    public NewsResponse archive(UUID id) { News news = find(id); news.archive(); return NewsResponse.from(news); }
    @Transactional
    public void delete(UUID id) { newsRepository.delete(find(id)); }

    private News find(UUID id) { return newsRepository.findDetailedById(id).orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada")); }
    private String uniqueSlug(String title, UUID currentId) {
        String base = SlugUtils.from(title); String candidate = base; int suffix = 2;
        while (currentId == null ? newsRepository.existsBySlug(candidate) : newsRepository.existsBySlugAndIdNot(candidate, currentId)) candidate = base + "-" + suffix++;
        return candidate;
    }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
