package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CambiarEstadoInsumoQuimicoService implements CambiarEstadoInsumoQuimicoUseCase {

    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    public CambiarEstadoInsumoQuimicoService(InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
    }

    @Override
    public InsumoQuimicoResult ejecutar(Long insumoId, boolean activo) {
        InsumoQuimico insumo = insumoQuimicoRepositoryPort.buscarPorId(insumoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Insumo quimico", insumoId));

        if (activo) {
            insumo.activar();
        } else {
            insumo.desactivar();
        }

        return InsumoQuimicoAssembler.toResult(insumoQuimicoRepositoryPort.guardar(insumo));
    }
}
