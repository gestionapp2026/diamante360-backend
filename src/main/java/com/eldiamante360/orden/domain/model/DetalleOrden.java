package com.eldiamante360.orden.domain.model;

import com.eldiamante360.orden.domain.exception.CantidadOrdenInvalidaException;

import java.math.BigDecimal;

/**
 * Linea de una orden. El nombre del producto se "congela" (snapshot) en el
 * momento de crear la orden, igual que en factura, para que la orden no
 * cambie si despues se edita el nombre del producto en el catalogo. A
 * diferencia de DetalleFactura, no maneja precio ni descuento: la orden es
 * solo una intencion de venta, el precio se define hasta que se facture.
 */
public record DetalleOrden(
        Long id,
        Long productoId,
        String productoNombre,
        BigDecimal cantidad
) {

    public static DetalleOrden nuevo(Long productoId, String productoNombre, BigDecimal cantidad) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CantidadOrdenInvalidaException();
        }
        return new DetalleOrden(null, productoId, productoNombre, cantidad);
    }
}
