package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarInsumosQuimicosService implements ListarInsumosQuimicosUseCase {

    private final InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    public ListarInsumosQuimicosService(InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort) {
        this.insumoQuimicoRepositoryPort = insumoQuimicoRepositoryPort;
    }

    @Override
    public Page<InsumoQuimicoResult> ejecutar(Pageable pageable) {
        return insumoQuimicoRepositoryPort.listar(pageable).map(InsumoQuimicoAssembler::toResult);
    }
}
