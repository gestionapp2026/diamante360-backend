package com.eldiamante360.auth.presentation.dto.response;

import java.util.Set;

public record UsuarioSesionResponse(
        Long id,
        String username,
        String nombreCompleto,
        String rol,
        Set<String> permisos,
        boolean debeCambiarPassword
) {
}
