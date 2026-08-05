package com.eldiamante360.factura.domain.model;

import java.time.Instant;

public record HistorialFactura(
        Long id,
        Long facturaId,
        TipoEventoFactura tipoEvento,
        String descripcion,
        Long usuarioId,
        Instant fecha
) {

    public static HistorialFactura nuevo(Long facturaId, TipoEventoFactura tipoEvento, String descripcion,
                                          Long usuarioId) {
        return new HistorialFactura(null, facturaId, tipoEvento, descripcion, usuarioId, Instant.now());
    }
}
