package com.eldiamante360.factura.application.dto;

import com.eldiamante360.factura.domain.model.TipoEventoFactura;

import java.time.Instant;

public record HistorialFacturaResult(
        Long id,
        Long facturaId,
        TipoEventoFactura tipoEvento,
        String descripcion,
        Long usuarioId,
        Instant fecha
) {
}
