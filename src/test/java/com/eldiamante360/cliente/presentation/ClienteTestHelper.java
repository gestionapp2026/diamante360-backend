package com.eldiamante360.cliente.presentation;

import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
import com.eldiamante360.cliente.presentation.dto.request.CrearClienteRequest;
import com.eldiamante360.cliente.presentation.dto.request.CrearRutaRequest;
import com.eldiamante360.cliente.presentation.dto.response.ClienteResponse;
import com.eldiamante360.cliente.presentation.dto.response.RutaResponse;
import com.eldiamante360.shared.it.AuthTestHelper;
import com.eldiamante360.shared.it.TestDataFactory;

/**
 * Utilidades para crear Clientes y Rutas de prueba (via API real, autenticado
 * como ADMIN) reutilizables entre {@link ClienteControllerIT}, {@link RutaControllerIT},
 * {@link ClienteDetalleControllerIT} y, al ser publica, modulos posteriores
 * que dependen de un Cliente existente para sembrar sus propios datos
 * (p.ej. Deudores/Facturacion necesitan un clienteId real).
 */
public final class ClienteTestHelper {

    private ClienteTestHelper() {
    }

    public static RutaResponse crearRutaActiva(String accessTokenAdmin) {
        var request = new CrearRutaRequest(TestDataFactory.nombreCompleto("Ruta IT"), "Ruta de prueba IT");
        return AuthTestHelper.autenticado(accessTokenAdmin)
                .body(request)
                .when().post("/rutas")
                .then().statusCode(201)
                .extract().as(RutaResponse.class);
    }

    public static ClienteResponse crearCliente(String accessTokenAdmin, Long rutaId) {
        var request = new CrearClienteRequest(
                TipoDocumentoCliente.CC,
                TestDataFactory.numeroDocumento(),
                TestDataFactory.nombreCompleto("Cliente IT"),
                TestDataFactory.telefonos(),
                TestDataFactory.email("cliente.it"),
                "Calle IT 123",
                rutaId);
        return AuthTestHelper.autenticado(accessTokenAdmin)
                .body(request)
                .when().post("/clientes")
                .then().statusCode(201)
                .extract().as(ClienteResponse.class);
    }

    public static ClienteResponse crearClienteMinimo(String accessTokenAdmin) {
        var request = new CrearClienteRequest(
                TipoDocumentoCliente.CC,
                TestDataFactory.numeroDocumento(),
                TestDataFactory.nombreCompleto("Cliente Minimo IT"),
                null, null, null, null);
        return AuthTestHelper.autenticado(accessTokenAdmin)
                .body(request)
                .when().post("/clientes")
                .then().statusCode(201)
                .extract().as(ClienteResponse.class);
    }
}
