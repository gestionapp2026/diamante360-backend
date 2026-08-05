package com.eldiamante360.cliente.application.dto;

public record ActualizarClienteCommand(
        Long clienteId,
        String nombre,
        String telefono,
        String email,
        String direccion,
        Long usuarioId
) {
}
