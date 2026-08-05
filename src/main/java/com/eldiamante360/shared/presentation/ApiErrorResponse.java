package com.eldiamante360.shared.presentation;

import java.time.Instant;
import java.util.List;

/**
 * Forma estandar de toda respuesta de error de la API.
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<CampoError> errores
) {

    public record CampoError(String campo, String mensaje) {
    }

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(Instant.now(), status, error, message, path, List.of());
    }

    public static ApiErrorResponse of(int status, String error, String message, String path, List<CampoError> errores) {
        return new ApiErrorResponse(Instant.now(), status, error, message, path, errores);
    }
}
