package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.UsuarioResult;

public interface ObtenerUsuarioUseCase {

    UsuarioResult ejecutar(Long usuarioId);
}
