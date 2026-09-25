package br.com.maravilhasnoticias.backend.news;

import org.springframework.data.jpa.domain.Specification;

public final class NewsSpecifications {
    private NewsSpecifications() {}
    public static Specification<News> status(NewsStatus status) { return (root, query, cb) -> cb.equal(root.get("status"), status); }
    public static Specification<News> category(String slug) { return (root, query, cb) -> slug == null || slug.isBlank() ? cb.conjunction() : cb.equal(root.get("category").get("slug"), slug); }
    public static Specification<News> featured() { return (root, query, cb) -> cb.isTrue(root.get("featured")); }
    public static Specification<News> text(String q) { return (root, query, cb) -> {
        if (q == null || q.isBlank()) return cb.conjunction();
        String term = "%" + q.trim().toLowerCase() + "%";
        return cb.or(cb.like(cb.lower(root.get("title")), term), cb.like(cb.lower(root.get("summary")), term));
    }; }
}
