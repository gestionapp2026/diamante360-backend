package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.ActualizarFormulaCommand;
import com.eldiamante360.formula.application.dto.FormulaResult;

public interface ActualizarFormulaUseCase {

    FormulaResult ejecutar(ActualizarFormulaCommand command);
}
