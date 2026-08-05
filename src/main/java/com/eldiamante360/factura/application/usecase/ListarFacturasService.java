package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarFacturasService implements ListarFacturasUseCase {

    private final FacturaRepositoryPort facturaRepositoryPort;

    public ListarFacturasService(FacturaRepositoryPort facturaRepositoryPort) {
        this.facturaRepositoryPort = facturaRepositoryPort;
    }

    @Override
    public Page<FacturaResult> ejecutar(Pageable pageable) {
        return facturaRepositoryPort.listar(pageable).map(FacturaAssembler::toResult);
    }
}
