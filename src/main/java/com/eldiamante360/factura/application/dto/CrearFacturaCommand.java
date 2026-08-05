package com.eldiamante360.factura.application.dto;

import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.shared.domain.model.MedioPago;

import java.util.List;

public record CrearFacturaCommand(
        Long clienteId,
        TipoPago tipoPago,
        List<DetalleFacturaCommand> detalles,
        Long usuarioId,
        MedioPago medioPago
) {
}
