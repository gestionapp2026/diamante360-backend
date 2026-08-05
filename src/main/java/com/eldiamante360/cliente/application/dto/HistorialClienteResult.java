package com.eldiamante360.cliente.application.dto;

import com.eldiamante360.cliente.domain.model.TipoEventoCliente;

import java.time.Instant;

public record HistorialClienteResult(
        Long id,
        Long clienteId,
        TipoEventoCliente tipoEvento,
        String descripcion,
        Long usuarioId,
        Instant fecha
) {
}
