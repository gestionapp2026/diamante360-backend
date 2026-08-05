package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.FormulaResult;

public interface ObtenerFormulaPorProductoUseCase {

    FormulaResult ejecutar(Long productoId);
}
