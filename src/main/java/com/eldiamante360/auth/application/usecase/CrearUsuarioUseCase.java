package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.CrearUsuarioCommand;
import com.eldiamante360.auth.application.dto.UsuarioResult;

public interface CrearUsuarioUseCase {

    UsuarioResult ejecutar(CrearUsuarioCommand command);
}
