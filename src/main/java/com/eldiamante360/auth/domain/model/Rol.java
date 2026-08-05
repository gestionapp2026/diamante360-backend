package com.eldiamante360.auth.domain.model;

import java.util.Set;

/**
 * Rol del sistema (ADMIN, VENDEDOR) con su conjunto de permisos asociados.
 */
public record Rol(
        Long id,
        String nombre,
        String descripcion,
        Set<Permiso> permisos
) {

    public Set<String> codigosPermisos() {
        return permisos.stream()
                .map(Permiso::codigo)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
}
