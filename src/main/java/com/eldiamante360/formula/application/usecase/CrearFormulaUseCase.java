package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.CrearFormulaCommand;
import com.eldiamante360.formula.application.dto.FormulaResult;

public interface CrearFormulaUseCase {

    FormulaResult ejecutar(CrearFormulaCommand command);
}
