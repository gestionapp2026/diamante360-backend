package com.eldiamante360.auth.application.dto;

public record RefreshTokenCommand(String refreshTokenPlano, String ipOrigen, String userAgent) {
}
