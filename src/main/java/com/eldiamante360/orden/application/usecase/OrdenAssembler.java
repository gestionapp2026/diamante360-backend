package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.orden.application.dto.DetalleOrdenResult;
import com.eldiamante360.orden.application.dto.OrdenResult;
import com.eldiamante360.orden.domain.model.DetalleOrden;
import com.eldiamante360.orden.domain.model.Orden;

/**
 * Ensambla los DTOs de salida de aplicacion a partir del modelo de dominio.
 * No es un mapper de infraestructura: no conoce JPA ni MapStruct.
 */
final class OrdenAssembler {

    private OrdenAssembler() {
    }

    static OrdenResult toResult(Orden orden) {
        return new OrdenResult(
                orden.getId(),
                orden.getNumero(),
                orden.getClienteId(),
                orden.getClienteNombre(),
                orden.getFechaCreacion(),
                orden.getFechaEntrega(),
                orden.getEstado(),
                orden.getObservaciones(),
                orden.getDetalles().stream().map(OrdenAssembler::toResult).toList(),
                orden.getUsuarioId(),
                orden.getUsuarioNombre(),
                orden.getFechaDespacho(),
                orden.getFechaAnulacion()
        );
    }

    static DetalleOrdenResult toResult(DetalleOrden detalle) {
        return new DetalleOrdenResult(
                detalle.id(),
                detalle.productoId(),
                detalle.productoNombre(),
                detalle.cantidad()
        );
    }
}
