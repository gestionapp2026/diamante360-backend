package com.eldiamante360.cliente.application.dto;

import java.time.Instant;

public record ObservacionClienteResult(
        Long id,
        Long clienteId,
        String texto,
        Long usuarioId,
        Instant fecha
) {
}
