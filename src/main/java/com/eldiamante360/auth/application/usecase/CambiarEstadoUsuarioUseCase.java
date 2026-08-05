package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.UsuarioResult;

public interface CambiarEstadoUsuarioUseCase {

    /**
     * @param solicitanteId id del usuario autenticado que ejecuta la accion;
     *                       se usa para impedir que alguien se desactive a si mismo.
     */
    UsuarioResult ejecutar(Long usuarioId, boolean activo, Long solicitanteId);
}
