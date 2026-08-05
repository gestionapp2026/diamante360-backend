package com.eldiamante360.auth.presentation.dto.response;

import java.util.Set;

public record RolResponse(
        Long id,
        String nombre,
        String descripcion,
        Set<String> permisos
) {
}
