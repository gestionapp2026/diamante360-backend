package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.application.port.HistorialCuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.HistorialCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.TipoEventoCuentaPorCobrar;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Anula la cuenta por cobrar asociada a una factura. Se invoca internamente
 * desde el modulo factura (AnularFacturaService) cuando se anula una factura
 * a credito, en la misma transaccion.
 */
@Service
@Transactional
public class AnularCuentaPorCobrarService implements AnularCuentaPorCobrarUseCase {

    private final CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;
    private final HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort;

    public AnularCuentaPorCobrarService(CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort,
                                         HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort) {
        this.cuentaPorCobrarRepositoryPort = cuentaPorCobrarRepositoryPort;
        this.historialCuentaPorCobrarRepositoryPort = historialCuentaPorCobrarRepositoryPort;
    }

    @Override
    public CuentaPorCobrarResult ejecutar(Long facturaId, Long usuarioId) {
        CuentaPorCobrar cuenta = cuentaPorCobrarRepositoryPort.buscarPorFacturaId(facturaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("CuentaPorCobrar de la factura", facturaId));

        cuenta.anular();
        CuentaPorCobrar guardada = cuentaPorCobrarRepositoryPort.guardar(cuenta);

        historialCuentaPorCobrarRepositoryPort.guardar(HistorialCuentaPorCobrar.nuevo(guardada.getId(),
                TipoEventoCuentaPorCobrar.ANULACION,
                "Cuenta por cobrar anulada por anulacion de la factura " + guardada.getNumeroFactura(), usuarioId));

        return CuentaPorCobrarAssembler.toResult(guardada);
    }
}
