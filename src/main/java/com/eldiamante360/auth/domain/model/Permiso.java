package com.eldiamante360.auth.domain.model;

/**
 * Permiso granular del sistema (ej: PRODUCTO_CREAR). El codigo es la
 * autoridad usada por @PreAuthorize y por los claims del access token.
 */
public record Permiso(
        Long id,
        String codigo,
        String descripcion,
        String modulo
) {
}
