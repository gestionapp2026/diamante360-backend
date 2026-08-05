package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.port.FormulaRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ObtenerDependenciasFormulaService implements ObtenerDependenciasFormulaUseCase {

    private final FormulaRepositoryPort formulaRepositoryPort;

    public ObtenerDependenciasFormulaService(FormulaRepositoryPort formulaRepositoryPort) {
        this.formulaRepositoryPort = formulaRepositoryPort;
    }

    @Override
    public DependenciasResponse ejecutar(Long id) {
        formulaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Formula", id));

        return new DependenciasResponse(false, false, null, List.of());
    }
}
