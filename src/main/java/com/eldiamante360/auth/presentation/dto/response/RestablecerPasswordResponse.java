package com.eldiamante360.auth.presentation.dto.response;

public record RestablecerPasswordResponse(
        Long id,
        String username,
        String passwordTemporal
) {
}
