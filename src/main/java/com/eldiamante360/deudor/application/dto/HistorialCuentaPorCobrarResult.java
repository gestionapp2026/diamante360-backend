package com.eldiamante360.deudor.application.dto;

import com.eldiamante360.deudor.domain.model.TipoEventoCuentaPorCobrar;

import java.time.Instant;

public record HistorialCuentaPorCobrarResult(
        Long id,
        Long cuentaPorCobrarId,
        TipoEventoCuentaPorCobrar tipoEvento,
        String descripcion,
        Long usuarioId,
        Instant fecha
) {
}
