package br.com.maravilhasnoticias.backend.news;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<News, UUID>, JpaSpecificationExecutor<News> {
    boolean existsByCategoryId(UUID categoryId);
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, UUID id);
    @EntityGraph(attributePaths = {"category", "author"})
    Optional<News> findBySlugAndStatus(String slug, NewsStatus status);
    @EntityGraph(attributePaths = {"category", "author"})
    Optional<News> findDetailedById(UUID id);
}
