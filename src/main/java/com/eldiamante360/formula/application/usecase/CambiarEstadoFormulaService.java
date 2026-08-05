package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.dto.FormulaResult;
import com.eldiamante360.formula.application.port.FormulaRepositoryPort;
import com.eldiamante360.formula.domain.model.Formula;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CambiarEstadoFormulaService implements CambiarEstadoFormulaUseCase {

    private final FormulaRepositoryPort formulaRepositoryPort;
    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    public CambiarEstadoFormulaService(FormulaRepositoryPort formulaRepositoryPort,
                                        InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        this.formulaRepositoryPort = formulaRepositoryPort;
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
    }

    @Override
    public FormulaResult ejecutar(Long formulaId, boolean activo) {
        Formula formula = formulaRepositoryPort.buscarPorId(formulaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Formula", formulaId));

        if (activo) {
            formula.activar();
        } else {
            formula.desactivar();
        }

        return FormulaAssembler.toResult(formulaRepositoryPort.guardar(formula), insumoQuimicoRepositoryPort);
    }
}
