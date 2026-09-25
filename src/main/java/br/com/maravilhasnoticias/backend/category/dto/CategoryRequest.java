package br.com.maravilhasnoticias.backend.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve possuir no máximo 100 caracteres")
        String name,
        boolean active
) {
}
