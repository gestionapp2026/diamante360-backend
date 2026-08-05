package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.domain.model.EstadoFactura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarFacturasPorEstadoService implements ListarFacturasPorEstadoUseCase {

    private final FacturaRepositoryPort facturaRepositoryPort;

    public ListarFacturasPorEstadoService(FacturaRepositoryPort facturaRepositoryPort) {
        this.facturaRepositoryPort = facturaRepositoryPort;
    }

    @Override
    public Page<FacturaResult> ejecutar(EstadoFactura estado, Pageable pageable) {
        return facturaRepositoryPort.listarPorEstado(estado, pageable).map(FacturaAssembler::toResult);
    }
}
