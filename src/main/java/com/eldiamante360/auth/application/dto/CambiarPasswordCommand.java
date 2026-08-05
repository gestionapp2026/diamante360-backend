package com.eldiamante360.auth.application.dto;

public record CambiarPasswordCommand(
        Long usuarioId,
        String passwordActual,
        String passwordNueva
) {
}
