package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.ProduccionFormulaResult;
import com.eldiamante360.formula.application.dto.ProducirFormulaCommand;

public interface ProducirConFormulaUseCase {

    ProduccionFormulaResult ejecutar(ProducirFormulaCommand command);
}
