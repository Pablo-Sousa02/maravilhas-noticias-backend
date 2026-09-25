package br.com.maravilhasnoticias.backend.news;

import br.com.maravilhasnoticias.backend.category.Category;
import br.com.maravilhasnoticias.backend.user.User;
import br.com.maravilhasnoticias.backend.user.UserRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NewsTests {
    @Test
    void shouldKeepFirstPublishedAtWhenRepublishing() {
        News news = new News("Título", "titulo", "Resumo", "Conteúdo", null, false, true,
                new Category("Geral", "geral", true), new User("Admin", "admin@example.com", "hash", UserRole.ADMIN));

        assertThat(news.publish()).isTrue();
        var firstPublishedAt = news.getPublishedAt();
        assertThat(news.publish()).isFalse();

        assertThat(news.getStatus()).isEqualTo(NewsStatus.PUBLISHED);
        assertThat(news.getPublishedAt()).isEqualTo(firstPublishedAt);
    }
}
