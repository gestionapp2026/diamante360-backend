package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.shared.presentation.DependenciasResponse;

public interface ObtenerDependenciasRutaUseCase {

    DependenciasResponse ejecutar(Long id);
}
