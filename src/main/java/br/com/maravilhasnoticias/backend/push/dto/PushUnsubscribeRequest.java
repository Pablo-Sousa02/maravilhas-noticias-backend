package br.com.maravilhasnoticias.backend.push.dto;

import jakarta.validation.constraints.NotBlank;

public record PushUnsubscribeRequest(
        @NotBlank(message = "O endpoint é obrigatório") String endpoint
) {}
