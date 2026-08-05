package com.eldiamante360.factura.domain.model;

import com.eldiamante360.factura.domain.exception.CantidadFacturadaInvalidaException;
import com.eldiamante360.factura.domain.exception.PorcentajeDescuentoInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Linea de una factura. El precio unitario y el nombre del producto se
 * "congelan" (snapshot) en el momento de facturar, para que la factura no
 * cambie si despues se edita el precio o el nombre del producto en el
 * catalogo. Subtotal, descuento y total de la linea se calculan aqui mismo
 * y nunca se reciben ya calculados desde afuera.
 */
public record DetalleFactura(
        Long id,
        Long productoId,
        String productoNombre,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal porcentajeDescuento,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal total
) {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    public static DetalleFactura nuevo(Long productoId, String productoNombre, BigDecimal cantidad,
                                        BigDecimal precioUnitario, BigDecimal porcentajeDescuento) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CantidadFacturadaInvalidaException();
        }
        BigDecimal porcentaje = porcentajeDescuento != null ? porcentajeDescuento : BigDecimal.ZERO;
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(CIEN) > 0) {
            throw new PorcentajeDescuentoInvalidoException();
        }

        BigDecimal subtotalLinea = cantidad.multiply(precioUnitario).setScale(2, RoundingMode.HALF_UP);
        BigDecimal descuentoLinea = subtotalLinea.multiply(porcentaje)
                .divide(CIEN, 2, RoundingMode.HALF_UP);
        BigDecimal totalLinea = subtotalLinea.subtract(descuentoLinea);

        return new DetalleFactura(null, productoId, productoNombre, cantidad, precioUnitario, porcentaje,
                subtotalLinea, descuentoLinea, totalLinea);
    }
}
