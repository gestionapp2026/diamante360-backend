package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.shared.presentation.DependenciasResponse;

public interface ObtenerDependenciasUsuarioUseCase {

    DependenciasResponse ejecutar(Long id, Long usuarioActualId);
}
