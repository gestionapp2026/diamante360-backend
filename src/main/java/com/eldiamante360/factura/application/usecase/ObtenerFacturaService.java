package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerFacturaService implements ObtenerFacturaUseCase {

    private final FacturaRepositoryPort facturaRepositoryPort;

    public ObtenerFacturaService(FacturaRepositoryPort facturaRepositoryPort) {
        this.facturaRepositoryPort = facturaRepositoryPort;
    }

    @Override
    public FacturaResult ejecutar(Long id) {
        Factura factura = facturaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura", id));
        return FacturaAssembler.toResult(factura);
    }
}
