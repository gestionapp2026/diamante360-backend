package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.shared.presentation.DependenciasResponse;

public interface ObtenerDependenciasInsumoQuimicoUseCase {

    DependenciasResponse ejecutar(Long id);
}
