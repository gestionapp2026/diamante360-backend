package com.eldiamante360.auth.presentation.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiraEnSegundos,
        UsuarioSesionResponse usuario
) {
}
