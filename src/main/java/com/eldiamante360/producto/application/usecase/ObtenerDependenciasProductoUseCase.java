package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.shared.presentation.DependenciasResponse;

public interface ObtenerDependenciasProductoUseCase {

    DependenciasResponse ejecutar(Long id);
}
