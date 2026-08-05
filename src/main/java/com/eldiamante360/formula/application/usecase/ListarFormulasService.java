package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.FormulaResult;
import com.eldiamante360.formula.application.port.FormulaRepositoryPort;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarFormulasService implements ListarFormulasUseCase {

    private final FormulaRepositoryPort formulaRepositoryPort;
    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    public ListarFormulasService(FormulaRepositoryPort formulaRepositoryPort,
                                  InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        this.formulaRepositoryPort = formulaRepositoryPort;
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
    }

    @Override
    public Page<FormulaResult> ejecutar(Pageable pageable) {
        return formulaRepositoryPort.listar(pageable)
                .map(formula -> FormulaAssembler.toResult(formula, insumoQuimicoRepositoryPort));
    }
}
