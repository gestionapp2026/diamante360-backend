package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.shared.presentation.DependenciasResponse;

public interface ObtenerDependenciasFormulaUseCase {

    DependenciasResponse ejecutar(Long id);
}
