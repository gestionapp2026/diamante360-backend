package com.eldiamante360.factura.presentation.dto.response;

import com.eldiamante360.factura.domain.model.TipoEventoFactura;

import java.time.Instant;

public record HistorialFacturaResponse(
        Long id,
        Long facturaId,
        TipoEventoFactura tipoEvento,
        String descripcion,
        Long usuarioId,
        Instant fecha
) {
}
