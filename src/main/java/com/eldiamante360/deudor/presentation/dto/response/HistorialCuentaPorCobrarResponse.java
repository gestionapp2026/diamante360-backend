package com.eldiamante360.deudor.presentation.dto.response;

import com.eldiamante360.deudor.domain.model.TipoEventoCuentaPorCobrar;

import java.time.Instant;

public record HistorialCuentaPorCobrarResponse(
        Long id,
        Long cuentaPorCobrarId,
        TipoEventoCuentaPorCobrar tipoEvento,
        String descripcion,
        Long usuarioId,
        Instant fecha
) {
}
