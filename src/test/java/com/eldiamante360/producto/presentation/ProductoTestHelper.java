package com.eldiamante360.producto.presentation;

import com.eldiamante360.producto.domain.model.TipoVenta;
import com.eldiamante360.producto.domain.model.UnidadMedida;
import com.eldiamante360.producto.presentation.dto.request.CrearCategoriaRequest;
import com.eldiamante360.producto.presentation.dto.request.CrearProductoRequest;
import com.eldiamante360.producto.presentation.dto.response.CategoriaResponse;
import com.eldiamante360.producto.presentation.dto.response.ProductoResponse;
import com.eldiamante360.shared.it.AuthTestHelper;
import com.eldiamante360.shared.it.TestDataFactory;

import java.math.BigDecimal;

/**
 * Utilidades para crear Categorias y Productos de prueba (via API real,
 * autenticado como ADMIN). Publica porque, ademas de ser usada por las
 * futuras pruebas de integracion del modulo Productos y Categorias, la
 * necesita el modulo Deudores (a traves de Facturacion) para poder generar
 * una CuentaPorCobrar real: no existe ningun endpoint que cree una cuenta
 * por cobrar directamente, solo se genera como efecto de emitir una factura
 * a credito, y una factura requiere un producto real (ver
 * {@code com.eldiamante360.deudor.presentation.DeudorTestHelper}).
 */
public final class ProductoTestHelper {

    private ProductoTestHelper() {
    }

    public static CategoriaResponse crearCategoriaActiva(String accessTokenAdmin) {
        var request = new CrearCategoriaRequest(TestDataFactory.nombreCompleto("Categoria IT"), "Categoria de prueba IT");
        return AuthTestHelper.autenticado(accessTokenAdmin)
                .body(request)
                .when().post("/categorias")
                .then().statusCode(201)
                .extract().as(CategoriaResponse.class);
    }

    /** Crea un producto activo tipo UNIDAD con el precio de venta y stock indicados (ambos con escala <= 2). */
    public static ProductoResponse crearProducto(String accessTokenAdmin, Long categoriaId, BigDecimal precioVenta,
                                                  BigDecimal stockInicial) {
        var request = new CrearProductoRequest(
                TestDataFactory.nombreCompleto("Producto IT"),
                categoriaId,
                TipoVenta.UNIDAD,
                UnidadMedida.UND,
                precioVenta,
                precioVenta,
                stockInicial,
                BigDecimal.ZERO);
        return AuthTestHelper.autenticado(accessTokenAdmin)
                .body(request)
                .when().post("/productos")
                .then().statusCode(201)
                .extract().as(ProductoResponse.class);
    }

    /** Crea, en un solo paso, una categoria + un producto activo con stock amplio y el precio de venta indicado. */
    public static ProductoResponse crearProductoConCategoriaNueva(String accessTokenAdmin, BigDecimal precioVenta) {
        CategoriaResponse categoria = crearCategoriaActiva(accessTokenAdmin);
        return crearProducto(accessTokenAdmin, categoria.id(), precioVenta, new BigDecimal("1000"));
    }
}
