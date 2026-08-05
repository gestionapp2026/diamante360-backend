package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.AbonoResult;
import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.dto.HistorialCuentaPorCobrarResult;
import com.eldiamante360.deudor.domain.model.Abono;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.HistorialCuentaPorCobrar;

/**
 * Ensambla los DTOs de salida de aplicacion a partir del modelo de dominio.
 * No es un mapper de infraestructura: no conoce JPA ni MapStruct.
 */
final class CuentaPorCobrarAssembler {

    private CuentaPorCobrarAssembler() {
    }

    static CuentaPorCobrarResult toResult(CuentaPorCobrar cuenta) {
        return new CuentaPorCobrarResult(
                cuenta.getId(),
                cuenta.getFacturaId(),
                cuenta.getNumeroFactura(),
                cuenta.getClienteId(),
                cuenta.getClienteNombre(),
                cuenta.getClienteNumeroDocumento(),
                cuenta.getMontoOriginal(),
                cuenta.getSaldoPendiente(),
                cuenta.getEstado(),
                cuenta.getUsuarioId(),
                cuenta.getFecha(),
                cuenta.getFechaUltimoAbono(),
                cuenta.getFechaAnulacion()
        );
    }

    static AbonoResult toResult(Abono abono) {
        return new AbonoResult(
                abono.id(),
                abono.cuentaPorCobrarId(),
                abono.monto(),
                abono.usuarioId(),
                abono.fecha(),
                abono.medioPago()
        );
    }

    static HistorialCuentaPorCobrarResult toResult(HistorialCuentaPorCobrar historial) {
        return new HistorialCuentaPorCobrarResult(
                historial.id(),
                historial.cuentaPorCobrarId(),
                historial.tipoEvento(),
                historial.descripcion(),
                historial.usuarioId(),
                historial.fecha()
        );
    }
}
