package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.UsuarioResult;
import com.eldiamante360.auth.application.dto.UsuarioResumen;
import com.eldiamante360.auth.domain.model.Usuario;

/**
 * Ensambla los DTOs de salida de aplicacion a partir del modelo de dominio.
 * No es un mapper de infraestructura: no conoce JPA ni MapStruct.
 */
final class UsuarioAssembler {

    private UsuarioAssembler() {
    }

    static UsuarioResumen toResumen(Usuario usuario) {
        return new UsuarioResumen(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getRol().nombre(),
                usuario.getRol().codigosPermisos(),
                usuario.isDebeCambiarPassword()
        );
    }

    static UsuarioResult toResult(Usuario usuario) {
        return new UsuarioResult(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getRol().id(),
                usuario.getRol().nombre(),
                usuario.isActivo(),
                usuario.isDebeCambiarPassword(),
                usuario.getUltimoLogin()
        );
    }
}
