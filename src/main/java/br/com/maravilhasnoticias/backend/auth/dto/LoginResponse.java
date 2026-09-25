package br.com.maravilhasnoticias.backend.auth.dto;

import br.com.maravilhasnoticias.backend.user.dto.UserResponse;

public record LoginResponse(
        String token,
        String type,
        long expiresIn,
        UserResponse user
) {
}