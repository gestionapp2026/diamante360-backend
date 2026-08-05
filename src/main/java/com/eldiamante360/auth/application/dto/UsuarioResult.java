package com.eldiamante360.auth.application.dto;

import java.time.Instant;

public record UsuarioResult(
        Long id,
        String username,
        String nombreCompleto,
        Long rolId,
        String rolNombre,
        boolean activo,
        boolean debeCambiarPassword,
        Instant ultimoLogin
) {
}
