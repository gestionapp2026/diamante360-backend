package com.eldiamante360.factura.presentation.dto.response;

import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.shared.domain.model.MedioPago;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record FacturaResponse(
        Long id,
        String numero,
        Long clienteId,
        String clienteNombre,
        String clienteNumeroDocumento,
        TipoPago tipoPago,
        List<DetalleFacturaResponse> detalles,
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
