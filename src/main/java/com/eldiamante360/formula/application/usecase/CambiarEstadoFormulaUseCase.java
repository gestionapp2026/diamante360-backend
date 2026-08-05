package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.FormulaResult;

public interface CambiarEstadoFormulaUseCase {

    FormulaResult ejecutar(Long formulaId, boolean activo);
}
