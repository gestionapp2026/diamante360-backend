package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.dto.RegistrarCreditoCommand;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.application.port.HistorialCuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.HistorialCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.TipoEventoCuentaPorCobrar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registra la cuenta por cobrar que se origina cuando se emite una factura a
 * credito. Se invoca internamente desde el modulo factura (CrearFacturaService)
 * en la misma transaccion en que se guarda la factura; no se expone como una
 * entrada manual independiente, para que la unica fuente de creditos sea una
 * factura real.
 */
@Service
@Transactional
public class RegistrarCreditoService implements RegistrarCreditoUseCase {

    private final CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;
    private final HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort;

    public RegistrarCreditoService(CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort,
                                    HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort) {
        this.cuentaPorCobrarRepositoryPort = cuentaPorCobrarRepositoryPort;
        this.historialCuentaPorCobrarRepositoryPort = historialCuentaPorCobrarRepositoryPort;
    }

    @Override
    public CuentaPorCobrarResult ejecutar(RegistrarCreditoCommand command) {
        CuentaPorCobrar cuenta = CuentaPorCobrar.nueva(command.facturaId(), command.numeroFactura(),
                command.clienteId(), command.clienteNombre(), command.clienteNumeroDocumento(), command.monto(),
                command.usuarioId());
        CuentaPorCobrar guardada = cuentaPorCobrarRepositoryPort.guardar(cuenta);

        historialCuentaPorCobrarRepositoryPort.guardar(HistorialCuentaPorCobrar.nuevo(guardada.getId(),
                TipoEventoCuentaPorCobrar.CREACION,
                "Credito registrado por factura " + command.numeroFactura() + " por valor de " + command.monto(),
                command.usuarioId()));

        return CuentaPorCobrarAssembler.toResult(guardada);
    }
}
