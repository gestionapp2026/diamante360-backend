package com.eldiamante360.shared.domain.exception;

/**
 * Se intento eliminar definitivamente un recurso que todavia tiene
 * dependencias de negocio (facturas, ordenes, movimientos de kardex, cuentas
 * por cobrar, etc). Reusable entre todos los modulos que exponen un DELETE
 * fisico: el mensaje debe ser claro para mostrarse tal cual en el frontend.
 */
public class RecursoConDependenciasException extends ReglaNegocioException {

    public RecursoConDependenciasException(String mensaje) {
        super(mensaje);
    }
}
