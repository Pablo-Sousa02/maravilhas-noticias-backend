package br.com.maravilhasnoticias.backend.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlugUtilsTests {
    @Test
    void shouldCreateNormalizedSlug() {
        assertThat(SlugUtils.from("Maravilhas: Notícias da Região!"))
                .isEqualTo("maravilhas-noticias-da-regiao");
    }
}
