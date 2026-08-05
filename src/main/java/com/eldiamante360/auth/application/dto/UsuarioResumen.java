package com.eldiamante360.auth.application.dto;

import java.util.Set;

public record UsuarioResumen(
        Long id,
        String username,
        String nombreCompleto,
        String rol,
        Set<String> permisos,
        boolean debeCambiarPassword
) {
}
