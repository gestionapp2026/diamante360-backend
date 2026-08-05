package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.LoteResult;
import com.eldiamante360.insumoquimico.application.port.LoteInsumoRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarLotesPorInsumoService implements ListarLotesPorInsumoUseCase {

    private final LoteInsumoRepositoryPort loteInsumoRepositoryPort;

    public ListarLotesPorInsumoService(LoteInsumoRepositoryPort loteInsumoRepositoryPort) {
        this.loteInsumoRepositoryPort = loteInsumoRepositoryPort;
    }

    @Override
    public Page<LoteResult> ejecutar(Long insumoId, Pageable pageable) {
        return loteInsumoRepositoryPort.listarPorInsumo(insumoId, pageable)
                .map(InsumoQuimicoAssembler::toResult);
    }
}
