package com.eldiamante360.cliente.application.dto;

/**
 * rutaId nulo significa "quitar la ruta asignada" al cliente.
 */
public record AsignarRutaClienteCommand(
        Long clienteId,
        Long rutaId,
        Long usuarioId
) {
}
