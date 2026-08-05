package com.eldiamante360.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "El refresh token es obligatorio") String refreshToken
) {
}
