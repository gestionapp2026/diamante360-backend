package com.eldiamante360.shared.presentation;

import org.springframework.security.core.Authentication;

/**
 * Utilidades de seguridad compartidas entre controllers, usadas para
 * verificaciones adicionales de autorizacion que no encajan en un simple
 * {@code @PreAuthorize} (por ejemplo, enmascarar un campo sensible en la
 * respuesta segun los permisos del usuario autenticado).
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static boolean tieneAutoridad(Authentication authentication, String autoridad) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(autoridad));
    }
}
