package com.eldiamante360.cliente.application.dto;

public record RegistrarObservacionClienteCommand(
        Long clienteId,
        String texto,
        Long usuarioId
) {
}
