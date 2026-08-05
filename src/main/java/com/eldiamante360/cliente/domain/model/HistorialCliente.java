package com.eldiamante360.cliente.domain.model;

import java.time.Instant;

/**
 * Evento del historial de un cliente, generado automaticamente por los
 * casos de uso de aplicacion cada vez que se crea, edita, activa, desactiva
 * o se le cambia la ruta a un cliente. A diferencia de {@link ObservacionCliente},
 * no lo escribe un usuario directamente sino el propio sistema como
 * consecuencia de una accion.
 */
public record HistorialCliente(
        Long id,
        Long clienteId,
        TipoEventoCliente tipoEvento,
        String descripcion,
        Long usuarioId,
        Instant fecha
) {

    public static HistorialCliente nuevo(Long clienteId, TipoEventoCliente tipoEvento, String descripcion, Long usuarioId) {
        return new HistorialCliente(null, clienteId, tipoEvento, descripcion, usuarioId, Instant.now());
    }
}
