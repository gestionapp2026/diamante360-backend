package com.eldiamante360.cliente.application.dto;

import java.util.List;

public record ActualizarClienteCommand(
        Long clienteId,
        String nombre,
        List<String> telefonos,
        String email,
        String direccion,
        Long usuarioId
) {
}
