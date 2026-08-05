package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.FormulaResult;

public interface ObtenerFormulaUseCase {

    FormulaResult ejecutar(Long formulaId);
}
