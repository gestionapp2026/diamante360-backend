package com.eldiamante360.factura.presentation;

import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.factura.presentation.dto.request.CrearFacturaRequest;
import com.eldiamante360.factura.presentation.dto.request.DetalleFacturaRequest;
import com.eldiamante360.factura.presentation.dto.response.FacturaResponse;
import com.eldiamante360.shared.it.AuthTestHelper;

import java.math.BigDecimal;
import java.util.List;

/**
 * Utilidades para crear Facturas de prueba (via API real, autenticado como
 * ADMIN, que tiene FACTURA_CREAR). Publica porque, ademas de ser usada por
 * las futuras pruebas de integracion del modulo Facturacion, la necesita el
 * modulo Deudores: una CuentaPorCobrar solo se genera automaticamente al
 * emitir una factura con tipoPago=CREDITO (ver RegistrarCreditoService), no
 * existe ningun endpoint que la cree directamente.
 */
public final class FacturaTestHelper {

    private FacturaTestHelper() {
    }

    public static FacturaResponse crearFactura(String accessTokenAdmin, Long clienteId, Long productoId,
                                                TipoPago tipoPago, BigDecimal cantidad) {
        var detalle = new DetalleFacturaRequest(productoId, cantidad, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(clienteId, tipoPago, List.of(detalle), null);
        return AuthTestHelper.autenticado(accessTokenAdmin)
                .body(request)
                .when().post("/facturas")
                .then().statusCode(201)
                .extract().as(FacturaResponse.class);
    }

    /** Factura a credito de una sola linea (cantidad=1, sin descuento): el total coincide con el precioVenta del producto. */
    public static FacturaResponse crearFacturaCredito(String accessTokenAdmin, Long clienteId, Long productoId) {
        return crearFactura(accessTokenAdmin, clienteId, productoId, TipoPago.CREDITO, BigDecimal.ONE);
    }

    public static FacturaResponse crearFacturaContado(String accessTokenAdmin, Long clienteId, Long productoId) {
        return crearFactura(accessTokenAdmin, clienteId, productoId, TipoPago.CONTADO, BigDecimal.ONE);
    }
}
