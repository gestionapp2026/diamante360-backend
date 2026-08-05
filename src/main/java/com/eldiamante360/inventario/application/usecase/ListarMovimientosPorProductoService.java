package com.eldiamante360.inventario.application.usecase;

import com.eldiamante360.inventario.application.dto.MovimientoResult;
import com.eldiamante360.inventario.application.port.MovimientoInventarioRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarMovimientosPorProductoService implements ListarMovimientosPorProductoUseCase {

    private final MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort;

    public ListarMovimientosPorProductoService(MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort) {
        this.movimientoInventarioRepositoryPort = movimientoInventarioRepositoryPort;
    }

    @Override
    public Page<MovimientoResult> ejecutar(Long productoId, Pageable pageable) {
        return movimientoInventarioRepositoryPort.listarPorProducto(productoId, pageable)
                .map(MovimientoAssembler::toResult);
    }
}
