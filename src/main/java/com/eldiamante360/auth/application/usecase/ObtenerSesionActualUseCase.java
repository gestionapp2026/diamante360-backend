package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.UsuarioResumen;

public interface ObtenerSesionActualUseCase {

    UsuarioResumen ejecutar(String username);
}
