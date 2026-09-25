package br.com.maravilhasnoticias.backend.category.dto;

import br.com.maravilhasnoticias.backend.category.Category;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getSlug(),
                category.isActive(), category.getCreatedAt(), category.getUpdatedAt());
    }
}
