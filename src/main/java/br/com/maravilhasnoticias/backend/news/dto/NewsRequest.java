package br.com.maravilhasnoticias.backend.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record NewsRequest(
        @NotBlank(message = "O título é obrigatório") @Size(max = 180, message = "O título deve possuir no máximo 180 caracteres") String title,
        @NotBlank(message = "O resumo é obrigatório") @Size(max = 500, message = "O resumo deve possuir no máximo 500 caracteres") String summary,
        @NotBlank(message = "O conteúdo é obrigatório") String content,
        @Size(max = 1000, message = "A URL da imagem deve possuir no máximo 1000 caracteres") String coverImageUrl,
        boolean featured,
        boolean urgent,
        @NotNull(message = "A categoria é obrigatória") UUID categoryId
) {}
