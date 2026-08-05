package com.eldiamante360.factura.application.dto;

import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.shared.domain.model.MedioPago;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record FacturaResult(
        Long id,
        String numero,
        Long clienteId,
        String clienteNombre,
        String clienteNumeroDocumento,
        TipoPago tipoPago,
        List<DetalleFacturaResult> detalles,
        EstadoFactura estado,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal total,
        Long usuarioId,
        String usuarioNombre,
        Instant fecha,
        Instant fechaAnulacion,
        MedioPago medioPago
) {
}
