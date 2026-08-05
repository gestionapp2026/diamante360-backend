package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerCuentaPorCobrarPorFacturaService implements ObtenerCuentaPorCobrarPorFacturaUseCase {

    private final CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    public ObtenerCuentaPorCobrarPorFacturaService(CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort) {
        this.cuentaPorCobrarRepositoryPort = cuentaPorCobrarRepositoryPort;
    }

    @Override
    public CuentaPorCobrarResult ejecutar(Long facturaId) {
        CuentaPorCobrar cuenta = cuentaPorCobrarRepositoryPort.buscarPorFacturaId(facturaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("CuentaPorCobrar de la factura", facturaId));
        return CuentaPorCobrarAssembler.toResult(cuenta);
    }
}
