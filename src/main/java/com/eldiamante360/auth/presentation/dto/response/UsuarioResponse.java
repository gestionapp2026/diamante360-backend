package com.eldiamante360.auth.presentation.dto.response;

import java.time.Instant;

public record UsuarioResponse(
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
