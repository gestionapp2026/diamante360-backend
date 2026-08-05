package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.port.FormulaRepositoryPort;
import com.eldiamante360.formula.domain.model.Formula;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarFormulaService implements EliminarFormulaUseCase {

    private final FormulaRepositoryPort formulaRepositoryPort;

    public EliminarFormulaService(FormulaRepositoryPort formulaRepositoryPort) {
        this.formulaRepositoryPort = formulaRepositoryPort;
    }

    @Override
    public void ejecutar(Long id, boolean cascada) {
        Formula formula = formulaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Formula", id));

        formulaRepositoryPort.eliminar(formula.getId());
    }
}
