package com.eldiamante360.cliente.presentation.dto.response;

import java.time.Instant;

public record ObservacionClienteResponse(
        Long id,
        Long clienteId,
        String texto,
        Long usuarioId,
        Instant fecha
) {
}
