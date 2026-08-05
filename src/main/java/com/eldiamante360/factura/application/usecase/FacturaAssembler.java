package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.factura.application.dto.DetalleFacturaResult;
import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.application.dto.HistorialFacturaResult;
import com.eldiamante360.factura.domain.model.DetalleFactura;
import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.factura.domain.model.HistorialFactura;

/**
 * Ensambla los DTOs de salida de aplicacion a partir del modelo de dominio.
 * No es un mapper de infraestructura: no conoce JPA ni MapStruct.
 */
final class FacturaAssembler {

    private FacturaAssembler() {
    }

    static FacturaResult toResult(Factura factura) {
        return new FacturaResult(
                factura.getId(),
                factura.getNumero(),
                factura.getClienteId(),
                factura.getClienteNombre(),
                factura.getClienteNumeroDocumento(),
                factura.getTipoPago(),
                factura.getDetalles().stream().map(FacturaAssembler::toResult).toList(),
                factura.getEstado(),
                factura.getSubtotal(),
                factura.getDescuento(),
                factura.getTotal(),
                factura.getUsuarioId(),
                factura.getUsuarioNombre(),
                factura.getFecha(),
                factura.getFechaAnulacion(),
                factura.getMedioPago()
        );
    }

    static DetalleFacturaResult toResult(DetalleFactura detalle) {
        return new DetalleFacturaResult(
                detalle.id(),
                detalle.productoId(),
                detalle.productoNombre(),
                detalle.cantidad(),
                detalle.precioUnitario(),
                detalle.porcentajeDescuento(),
                detalle.subtotal(),
                detalle.descuento(),
                detalle.total()
        );
    }

    static HistorialFacturaResult toResult(HistorialFactura historial) {
        return new HistorialFacturaResult(
                historial.id(),
                historial.facturaId(),
                historial.tipoEvento(),
                historial.descripcion(),
                historial.usuarioId(),
                historial.fecha()
        );
    }
}
