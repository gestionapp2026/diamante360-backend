package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.shared.presentation.DependenciasResponse;

public interface ObtenerDependenciasOrdenUseCase {

    DependenciasResponse ejecutar(Long id);
}
