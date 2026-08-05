package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.HistorialFacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.application.port.HistorialFacturaRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarHistorialFacturaService implements ListarHistorialFacturaUseCase {

    private final FacturaRepositoryPort facturaRepositoryPort;
    private final HistorialFacturaRepositoryPort historialFacturaRepositoryPort;

    public ListarHistorialFacturaService(FacturaRepositoryPort facturaRepositoryPort,
                                          HistorialFacturaRepositoryPort historialFacturaRepositoryPort) {
        this.facturaRepositoryPort = facturaRepositoryPort;
        this.historialFacturaRepositoryPort = historialFacturaRepositoryPort;
    }

    @Override
    public Page<HistorialFacturaResult> ejecutar(Long facturaId, Pageable pageable) {
        facturaRepositoryPort.buscarPorId(facturaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura", facturaId));

        return historialFacturaRepositoryPort.listarPorFactura(facturaId, pageable).map(FacturaAssembler::toResult);
    }
}
