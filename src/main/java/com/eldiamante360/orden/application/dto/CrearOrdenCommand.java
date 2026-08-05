package com.eldiamante360.orden.application.dto;

import java.time.LocalDate;
import java.util.List;

public record CrearOrdenCommand(
        Long clienteId,
        LocalDate fechaEntrega,
        String observaciones,
        List<DetalleOrdenCommand> detalles,
        Long usuarioId
) {
}
