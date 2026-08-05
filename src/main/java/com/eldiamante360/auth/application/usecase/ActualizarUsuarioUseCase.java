package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.ActualizarUsuarioCommand;
import com.eldiamante360.auth.application.dto.UsuarioResult;

public interface ActualizarUsuarioUseCase {

    UsuarioResult ejecutar(ActualizarUsuarioCommand command);
}
