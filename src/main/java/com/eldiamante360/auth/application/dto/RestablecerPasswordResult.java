package com.eldiamante360.auth.application.dto;

public record RestablecerPasswordResult(
        Long id,
        String username,
        String passwordTemporal
) {
}
