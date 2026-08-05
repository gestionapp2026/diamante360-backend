package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.FormulaResult;
import com.eldiamante360.formula.application.port.FormulaRepositoryPort;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerFormulaService implements ObtenerFormulaUseCase {

    private final FormulaRepositoryPort formulaRepositoryPort;
    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    public ObtenerFormulaService(FormulaRepositoryPort formulaRepositoryPort,
                                  InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        this.formulaRepositoryPort = formulaRepositoryPort;
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
    }

    @Override
    public FormulaResult ejecutar(Long formulaId) {
        return formulaRepositoryPort.buscarPorId(formulaId)
                .map(formula -> FormulaAssembler.toResult(formula, insumoQuimicoRepositoryPort))
                .orElseThrow(() -> new RecursoNoEncontradoException("Formula", formulaId));
    }
}
