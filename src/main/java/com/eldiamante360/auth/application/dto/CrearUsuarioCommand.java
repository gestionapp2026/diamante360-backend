package com.eldiamante360.auth.application.dto;

public record CrearUsuarioCommand(
        String username,
        String password,
        String nombreCompleto,
        Long rolId
) {
}
