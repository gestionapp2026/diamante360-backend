package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.dto.RegistrarAbonoCommand;
import com.eldiamante360.deudor.application.port.AbonoRepositoryPort;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.application.port.HistorialCuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.Abono;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.HistorialCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.TipoEventoCuentaPorCobrar;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aplica un abono a una cuenta por cobrar, reduciendo el saldo pendiente. Si
 * el abono salda por completo la cuenta, el evento de historial queda como
 * PAGO_TOTAL en lugar de ABONO.
 */
@Service
@Transactional
public class RegistrarAbonoService implements RegistrarAbonoUseCase {

    private final CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;
    private final AbonoRepositoryPort abonoRepositoryPort;
    private final HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort;

    public RegistrarAbonoService(CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort,
                                  AbonoRepositoryPort abonoRepositoryPort,
                                  HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort) {
        this.cuentaPorCobrarRepositoryPort = cuentaPorCobrarRepositoryPort;
        this.abonoRepositoryPort = abonoRepositoryPort;
        this.historialCuentaPorCobrarRepositoryPort = historialCuentaPorCobrarRepositoryPort;
    }

    @Override
    public CuentaPorCobrarResult ejecutar(RegistrarAbonoCommand command) {
        CuentaPorCobrar cuenta = cuentaPorCobrarRepositoryPort.buscarPorId(command.cuentaPorCobrarId())
                .orElseThrow(() -> new RecursoNoEncontradoException("CuentaPorCobrar", command.cuentaPorCobrarId()));

        cuenta.registrarAbono(command.monto());
        CuentaPorCobrar guardada = cuentaPorCobrarRepositoryPort.guardar(cuenta);

        abonoRepositoryPort.guardar(Abono.nuevo(guardada.getId(), command.monto(), command.usuarioId(),
                command.medioPago()));

        boolean pagada = guardada.getEstado() == EstadoCuentaPorCobrar.PAGADA;
        historialCuentaPorCobrarRepositoryPort.guardar(HistorialCuentaPorCobrar.nuevo(guardada.getId(),
                pagada ? TipoEventoCuentaPorCobrar.PAGO_TOTAL : TipoEventoCuentaPorCobrar.ABONO,
                pagada
                        ? "Abono de " + command.monto() + " registrado, cuenta pagada en su totalidad"
                        : "Abono de " + command.monto() + " registrado, saldo pendiente " + guardada.getSaldoPendiente(),
                command.usuarioId()));

        return CuentaPorCobrarAssembler.toResult(guardada);
    }
}
