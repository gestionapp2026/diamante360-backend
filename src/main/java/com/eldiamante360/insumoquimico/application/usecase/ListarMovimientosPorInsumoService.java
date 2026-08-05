package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.MovimientoInsumoResult;
import com.eldiamante360.insumoquimico.application.port.MovimientoInsumoRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarMovimientosPorInsumoService implements ListarMovimientosPorInsumoUseCase {

    private final MovimientoInsumoRepositoryPort movimientoInsumoRepositoryPort;

    public ListarMovimientosPorInsumoService(MovimientoInsumoRepositoryPort movimientoInsumoRepositoryPort) {
        this.movimientoInsumoRepositoryPort = movimientoInsumoRepositoryPort;
    }

    @Override
    public Page<MovimientoInsumoResult> ejecutar(Long insumoId, Pageable pageable) {
        return movimientoInsumoRepositoryPort.listarPorInsumo(insumoId, pageable)
                .map(MovimientoInsumoAssembler::toResult);
    }
}
