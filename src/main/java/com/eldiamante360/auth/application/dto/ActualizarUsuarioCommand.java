package com.eldiamante360.auth.application.dto;

public record ActualizarUsuarioCommand(
        Long usuarioId,
        String nombreCompleto,
        Long rolId
) {
}
