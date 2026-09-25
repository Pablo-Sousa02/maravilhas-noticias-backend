package br.com.maravilhasnoticias.backend.news;

import br.com.maravilhasnoticias.backend.category.Category;
import br.com.maravilhasnoticias.backend.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {
    @Id @UuidGenerator private UUID id;
    @Column(nullable = false, length = 180) private String title;
    @Column(nullable = false, unique = true, length = 200) private String slug;
    @Column(nullable = false, length = 500) private String summary;
    @Column(nullable = false, columnDefinition = "TEXT") private String content;
    @Column(name = "cover_image_url", length = 1000) private String coverImageUrl;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private NewsStatus status;
    @Column(nullable = false) private boolean featured;
    @Column(nullable = false) private boolean urgent;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "category_id") private Category category;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "author_id") private User author;
    @Column(name = "published_at") private OffsetDateTime publishedAt;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private OffsetDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;

    public News(String title, String slug, String summary, String content, String coverImageUrl,
                boolean featured, boolean urgent, Category category, User author) {
        this.title = title; this.slug = slug; this.summary = summary; this.content = content;
        this.coverImageUrl = coverImageUrl; this.status = NewsStatus.DRAFT; this.featured = featured;
        this.urgent = urgent; this.category = category; this.author = author;
    }

    public void update(String title, String slug, String summary, String content, String coverImageUrl,
                       boolean featured, boolean urgent, Category category) {
        this.title = title; this.slug = slug; this.summary = summary; this.content = content;
        this.coverImageUrl = coverImageUrl; this.featured = featured; this.urgent = urgent; this.category = category;
    }
    public boolean publish() {
        boolean firstPublication = publishedAt == null;
        status = NewsStatus.PUBLISHED;
        if (firstPublication) publishedAt = OffsetDateTime.now();
        return firstPublication;
    }
    public void archive() { status = NewsStatus.ARCHIVED; }
}
