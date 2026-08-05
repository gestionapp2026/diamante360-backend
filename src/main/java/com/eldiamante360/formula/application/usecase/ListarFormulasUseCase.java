package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.FormulaResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarFormulasUseCase {

    Page<FormulaResult> ejecutar(Pageable pageable);
}
