package br.com.maravilhasnoticias.backend.news.dto;

import br.com.maravilhasnoticias.backend.category.dto.CategoryResponse;
import br.com.maravilhasnoticias.backend.news.News;
import br.com.maravilhasnoticias.backend.news.NewsStatus;
import br.com.maravilhasnoticias.backend.user.dto.UserResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NewsResponse(UUID id, String title, String slug, String summary, String content,
                           String coverImageUrl, NewsStatus status, boolean featured, boolean urgent,
                           CategoryResponse category, UserResponse author, OffsetDateTime publishedAt,
                           OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    public static NewsResponse from(News news) {
        return new NewsResponse(news.getId(), news.getTitle(), news.getSlug(), news.getSummary(), news.getContent(),
                news.getCoverImageUrl(), news.getStatus(), news.isFeatured(), news.isUrgent(),
                CategoryResponse.from(news.getCategory()), UserResponse.from(news.getAuthor()), news.getPublishedAt(),
                news.getCreatedAt(), news.getUpdatedAt());
    }
}
