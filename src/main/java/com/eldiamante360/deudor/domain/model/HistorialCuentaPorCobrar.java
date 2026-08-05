package com.eldiamante360.deudor.domain.model;

import java.time.Instant;

public record HistorialCuentaPorCobrar(
        Long id,
        Long cuentaPorCobrarId,
        TipoEventoCuentaPorCobrar tipoEvento,
        String descripcion,
        Long usuarioId,
        Instant fecha
) {

    public static HistorialCuentaPorCobrar nuevo(Long cuentaPorCobrarId, TipoEventoCuentaPorCobrar tipoEvento,
                                                   String descripcion, Long usuarioId) {
        return new HistorialCuentaPorCobrar(null, cuentaPorCobrarId, tipoEvento, descripcion, usuarioId,
                Instant.now());
    }
}
