package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerInsumoQuimicoService implements ObtenerInsumoQuimicoUseCase {

    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    public ObtenerInsumoQuimicoService(InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
    }

    @Override
    public InsumoQuimicoResult ejecutar(Long insumoId) {
        return insumoQuimicoRepositoryPort.buscarPorId(insumoId)
                .map(InsumoQuimicoAssembler::toResult)
                .orElseThrow(() -> new RecursoNoEncontradoException("Insumo quimico", insumoId));
    }
}
