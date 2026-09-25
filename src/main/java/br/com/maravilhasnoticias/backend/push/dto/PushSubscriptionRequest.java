package br.com.maravilhasnoticias.backend.push.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PushSubscriptionRequest(
        @NotBlank(message = "O endpoint é obrigatório") String endpoint,
        @NotNull(message = "As chaves são obrigatórias") @Valid Keys keys
) {
    public record Keys(
            @NotBlank(message = "A chave p256dh é obrigatória") String p256dh,
            @NotBlank(message = "A chave auth é obrigatória") String auth
    ) {}
}
