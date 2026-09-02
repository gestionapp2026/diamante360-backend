package com.eldiamante360.auth.application.dto;

public record ActualizarUsuarioCommand(
        Long usuarioId,
        String username,
        String nombreCompleto,
        Long rolId
) {
}
