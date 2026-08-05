package com.eldiamante360.auth.application.dto;

public record SesionResult(
        String accessToken,
        String refreshToken,
        long expiraEnSegundos,
        UsuarioResumen usuario
) {
}
