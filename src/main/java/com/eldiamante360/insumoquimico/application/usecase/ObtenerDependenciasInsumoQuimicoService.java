package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.formula.infrastructure.persistence.repository.DetalleFormulaJpaRepository;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.infrastructure.persistence.repository.MovimientoInsumoJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.DependenciasResponse.ConteoDependencia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ObtenerDependenciasInsumoQuimicoService implements ObtenerDependenciasInsumoQuimicoUseCase {

    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;
    private final MovimientoInsumoJpaRepository movimientoInsumoJpaRepository;
    private final DetalleFormulaJpaRepository detalleFormulaJpaRepository;

    public ObtenerDependenciasInsumoQuimicoService(InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort,
                                                     MovimientoInsumoJpaRepository movimientoInsumoJpaRepository,
                                                     DetalleFormulaJpaRepository detalleFormulaJpaRepository) {
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
        this.movimientoInsumoJpaRepository = movimientoInsumoJpaRepository;
        this.detalleFormulaJpaRepository = detalleFormulaJpaRepository;
    }

    @Override
    public DependenciasResponse ejecutar(Long id) {
        var insumo = insumoQuimicoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Insumo quimico", id));

        long movimientos = movimientoInsumoJpaRepository.countByInsumoId(insumo.getId());
        long formulasQueLoUsan = detalleFormulaJpaRepository.countByInsumoId(insumo.getId());

        List<ConteoDependencia> conteos = new ArrayList<>();
        if (movimientos > 0) {
            conteos.add(new ConteoDependencia("movimientos", "Movimientos de kardex", movimientos));
        }
        if (formulasQueLoUsan > 0) {
            conteos.add(new ConteoDependencia("formulasQueLoUsan", "Formulas que lo usan como ingrediente", formulasQueLoUsan));
        }

        boolean tieneDependencias = !conteos.isEmpty();
        return new DependenciasResponse(tieneDependencias, false, null, conteos);
    }
}
