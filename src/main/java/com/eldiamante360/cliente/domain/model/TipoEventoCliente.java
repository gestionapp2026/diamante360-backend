package com.eldiamante360.cliente.domain.model;

/**
 * Tipo de evento registrado automaticamente en el historial de un cliente
 * cada vez que un caso de uso modifica su estado o sus datos.
 */
public enum TipoEventoCliente {
    CREACION,
    EDICION,
    CAMBIO_RUTA,
    ACTIVACION,
    DESACTIVACION
}
