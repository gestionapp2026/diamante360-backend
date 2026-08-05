package com.eldiamante360.deudor.presentation;

import com.eldiamante360.cliente.presentation.ClienteTestHelper;
import com.eldiamante360.cliente.presentation.dto.response.ClienteResponse;
import com.eldiamante360.deudor.presentation.dto.response.CuentaPorCobrarResponse;
import com.eldiamante360.factura.presentation.FacturaTestHelper;
import com.eldiamante360.factura.presentation.dto.response.FacturaResponse;
import com.eldiamante360.producto.presentation.ProductoTestHelper;
import com.eldiamante360.producto.presentation.dto.response.ProductoResponse;
import com.eldiamante360.shared.it.AuthTestHelper;

import java.math.BigDecimal;

/**
 * Utilidad para sembrar cuentas por cobrar de prueba para el modulo Deudores.
 *
 * <p><b>Por que no se crea la CuentaPorCobrar "a mano":</b> segun
 * {@code RegistrarCreditoService}, una cuenta por cobrar SOLO se crea como
 * efecto de emitir una Factura con tipoPago=CREDITO (invocado internamente
 * por {@code CrearFacturaService} en la misma transaccion); no existe, ni
 * deberia existir, un endpoint que la cree de forma independiente ("para que
 * la unica fuente de creditos sea una factura real", segun el comentario de
 * ese servicio). Aunque el orden de modulos acordado ubica a Deudores (4)
 * antes que Productos/Categorias (5) y Facturacion (8), el codigo de
 * produccion de esos modulos posteriores YA esta implementado (solo faltan
 * sus propias pruebas de integracion, que se escribiran mas adelante
 * respetando el orden). Por lo tanto, en vez de inyectar un caso de uso
 * interno (RegistrarCreditoUseCase) o insertar filas directamente por
 * repositorio -algo que un cliente real de la API nunca podria hacer-, estas
 * pruebas siembran la cuenta por cobrar recorriendo el flujo de negocio real
 * completo por HTTP: crear categoria -> crear producto -> crear cliente (o
 * usar uno existente) -> emitir factura a credito. Esto mantiene el mandato
 * de "HTTP real, nunca MockMvc ni atajos internos" incluso para los datos de
 * siembra.
 */
final class DeudorTestHelper {

    private DeudorTestHelper() {
    }

    /** Cliente nuevo + producto nuevo (precioVenta = monto exacto) + factura a credito de 1 unidad. */
    static FacturaResponse crearFacturaCreditoParaClienteNuevo(String accessTokenAdmin, BigDecimal monto) {
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(accessTokenAdmin);
        return crearFacturaCreditoParaCliente(accessTokenAdmin, cliente.id(), monto);
    }

    /** Producto nuevo (precioVenta = monto exacto) + factura a credito de 1 unidad para el cliente indicado. */
    static FacturaResponse crearFacturaCreditoParaCliente(String accessTokenAdmin, Long clienteId, BigDecimal monto) {
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(accessTokenAdmin, monto);
        return FacturaTestHelper.crearFacturaCredito(accessTokenAdmin, clienteId, producto.id());
    }

    /**
     * Siembra una cuenta por cobrar completa para un cliente nuevo y la
     * devuelve consultandola por HTTP real (GET /deudores/factura/{facturaId}),
     * ejercitando de paso ese endpoint.
     */
    static CuentaPorCobrarResponse crearCuentaPorCobrar(String accessTokenAdmin, BigDecimal monto) {
        FacturaResponse factura = crearFacturaCreditoParaClienteNuevo(accessTokenAdmin, monto);
        return obtenerPorFactura(accessTokenAdmin, factura.id());
    }

    static CuentaPorCobrarResponse crearCuentaPorCobrarParaCliente(String accessTokenAdmin, Long clienteId, BigDecimal monto) {
        FacturaResponse factura = crearFacturaCreditoParaCliente(accessTokenAdmin, clienteId, monto);
        return obtenerPorFactura(accessTokenAdmin, factura.id());
    }

    private static CuentaPorCobrarResponse obtenerPorFactura(String accessTokenAdmin, Long facturaId) {
        return AuthTestHelper.autenticado(accessTokenAdmin)
                .when().get("/deudores/factura/{facturaId}", facturaId)
                .then().statusCode(200)
                .extract().as(CuentaPorCobrarResponse.class);
    }
}
