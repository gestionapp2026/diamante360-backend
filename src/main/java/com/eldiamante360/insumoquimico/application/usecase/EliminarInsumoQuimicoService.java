package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.formula.infrastructure.persistence.repository.DetalleFormulaJpaRepository;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.infrastructure.persistence.repository.MovimientoInsumoJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarInsumoQuimicoService implements EliminarInsumoQuimicoUseCase {

    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;
    private final MovimientoInsumoJpaRepository movimientoInsumoJpaRepository;
    private final DetalleFormulaJpaRepository detalleFormulaJpaRepository;

    public EliminarInsumoQuimicoService(InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort,
                                         MovimientoInsumoJpaRepository movimientoInsumoJpaRepository,
                                         DetalleFormulaJpaRepository detalleFormulaJpaRepository) {
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
        this.movimientoInsumoJpaRepository = movimientoInsumoJpaRepository;
        this.detalleFormulaJpaRepository = detalleFormulaJpaRepository;
    }

    @Override
    public void ejecutar(Long id, boolean cascada) {
        InsumoQuimico insumo = insumoQuimicoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Insumo quimico", id));

        boolean tieneDependencias = movimientoInsumoJpaRepository.existsByInsumoId(insumo.getId())
                || detalleFormulaJpaRepository.existsByInsumoId(insumo.getId());

        if (tieneDependencias && !cascada) {
            throw new RecursoConDependenciasException(
                    "No se puede eliminar: el insumo quimico tiene movimientos o formulas asociadas.");
        }

        if (cascada) {
            movimientoInsumoJpaRepository.deleteByInsumoId(insumo.getId());
            detalleFormulaJpaRepository.deleteByInsumoId(insumo.getId());
        }

        insumoQuimicoRepositoryPort.eliminar(insumo.getId());
    }
}
