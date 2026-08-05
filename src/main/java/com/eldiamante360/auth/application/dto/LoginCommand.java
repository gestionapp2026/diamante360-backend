package com.eldiamante360.auth.application.dto;

public record LoginCommand(String username, String password, String ipOrigen, String userAgent) {
}
