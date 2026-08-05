package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.RestablecerPasswordResult;

public interface RestablecerPasswordUseCase {

    /**
     * Genera una contrasena temporal para el usuario indicado y lo marca para
     * que deba definir una nueva en su proximo login.
     *
     * @param solicitanteId id del usuario autenticado (admin) que ejecuta la accion;
     *                       se usa para impedir que alguien se restablezca su propia contrasena
     *                       (para eso ya existe "cambiar mi contrasena").
     */
    RestablecerPasswordResult ejecutar(Long usuarioId, Long solicitanteId);
}
