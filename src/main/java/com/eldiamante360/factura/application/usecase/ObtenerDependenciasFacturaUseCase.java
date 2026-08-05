package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.shared.presentation.DependenciasResponse;

public interface ObtenerDependenciasFacturaUseCase {

    DependenciasResponse ejecutar(Long id);
}
