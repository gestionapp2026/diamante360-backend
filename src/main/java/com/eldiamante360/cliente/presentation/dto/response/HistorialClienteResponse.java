package com.eldiamante360.cliente.presentation.dto.response;

import com.eldiamante360.cliente.domain.model.TipoEventoCliente;

import java.time.Instant;

public record HistorialClienteResponse(
        Long id,
        Long clienteId,
        TipoEventoCliente tipoEvento,
        String descripcion,
        Long usuarioId,
        Instant fecha
) {
}
