package com.eldiamante360.cliente.application.dto;

public record ActualizarRutaCommand(
        Long rutaId,
        String nombre,
        String descripcion
) {
}
