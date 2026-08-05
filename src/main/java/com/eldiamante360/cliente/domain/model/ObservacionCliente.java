package com.eldiamante360.cliente.domain.model;

import java.time.Instant;

/**
 * Nota de texto libre registrada manualmente por un usuario sobre un
 * cliente (ej. preferencias, incidencias, acuerdos comerciales). Es un
 * registro inmutable: no se edita ni se elimina una vez creado.
 */
public record ObservacionCliente(
        Long id,
        Long clienteId,
        String texto,
        Long usuarioId,
        Instant fecha
) {

    public static ObservacionCliente nueva(Long clienteId, String texto, Long usuarioId) {
        return new ObservacionCliente(null, clienteId, texto, usuarioId, Instant.now());
    }
}
