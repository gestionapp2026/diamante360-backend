package com.eldiamante360.cliente.presentation;

import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.cliente.domain.model.TipoEventoCliente;
import com.eldiamante360.cliente.presentation.dto.request.ActualizarClienteRequest;
import com.eldiamante360.cliente.presentation.dto.request.AsignarRutaRequest;
import com.eldiamante360.cliente.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.cliente.presentation.dto.request.RegistrarObservacionRequest;
import com.eldiamante360.cliente.presentation.dto.response.ClienteResponse;
import com.eldiamante360.cliente.presentation.dto.response.HistorialClienteResponse;
import com.eldiamante360.cliente.presentation.dto.response.ObservacionClienteResponse;
import com.eldiamante360.cliente.presentation.dto.response.RutaResponse;
import com.eldiamante360.shared.it.ApiErrorAssertions;
import com.eldiamante360.shared.it.AuthTestHelper;
import com.eldiamante360.shared.it.BaseIntegrationTest;
import com.eldiamante360.shared.it.TestDataFactory;
import com.eldiamante360.shared.presentation.ApiErrorResponse;
import com.eldiamante360.shared.presentation.PageResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integracion de {@link ClienteDetalleController} (observaciones e historial).
 */
class ClienteDetalleControllerIT extends BaseIntegrationTest {

    // ---------------------------------------------------------------
    // POST /clientes/{clienteId}/observaciones
    // ---------------------------------------------------------------

    @Test
    void registrarObservacion_datosValidos_devuelve201ConTextoYUsuarioId() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var request = new RegistrarObservacionRequest("Cliente solicito cambio de horario de entrega.");

        ObservacionClienteResponse observacion = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/clientes/{clienteId}/observaciones", cliente.id())
                .then().statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(ObservacionClienteResponse.class);

        assertThat(observacion.id()).isNotNull();
        assertThat(observacion.clienteId()).isEqualTo(cliente.id());
        assertThat(observacion.texto()).isEqualTo(request.texto());
        assertThat(observacion.usuarioId()).isEqualTo(admin.usuario().id());
        assertThat(observacion.fecha()).isNotNull();
    }

    @Test
    void registrarObservacion_clienteInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarObservacionRequest("Observacion huerfana"))
                .when().post("/clientes/999999999/observaciones")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void registrarObservacion_textoVacio_devuelve400() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarObservacionRequest(""))
                .when().post("/clientes/{clienteId}/observaciones", cliente.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "texto");
    }

    @Test
    void registrarObservacion_comoVendedorConPermisoClienteCrear_devuelve201() {
        // VENDEDOR tiene CLIENTE_CREAR (V21__permisos_vendedor_crear_cliente_producto.sql), que este
        // endpoint reutiliza para registrar observaciones.
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new RegistrarObservacionRequest("Observacion de vendedor"))
                .when().post("/clientes/{clienteId}/observaciones", cliente.id())
                .then().statusCode(201);
    }

    // ---------------------------------------------------------------
    // GET /clientes/{clienteId}/observaciones
    // ---------------------------------------------------------------

    @Test
    void listarObservaciones_comoAdmin_devuelvePaginaConLaObservacionRegistrada() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarObservacionRequest("Primera observacion de prueba"))
                .when().post("/clientes/{clienteId}/observaciones", cliente.id())
                .then().statusCode(201);

        PageResponse<ObservacionClienteResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/clientes/{clienteId}/observaciones", cliente.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido()).hasSize(1);
        assertThat(pagina.contenido().get(0).texto()).isEqualTo("Primera observacion de prueba");
    }

    @Test
    void listarObservaciones_clienteInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/clientes/999999999/observaciones")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    // ---------------------------------------------------------------
    // GET /clientes/{clienteId}/historial
    // ---------------------------------------------------------------

    @Test
    void listarHistorial_incluyeEventoCreacionAlCrearElCliente() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        PageResponse<HistorialClienteResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/clientes/{clienteId}/historial", cliente.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(HistorialClienteResponse::tipoEvento)
                .contains(TipoEventoCliente.CREACION);
    }

    @Test
    void listarHistorial_incluyeEventoEdicionAlActualizarDatos() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        AuthTestHelper.autenticado(admin.accessToken())
                .body(new ActualizarClienteRequest(TestDataFactory.nombreCompleto("Cliente Editado"), null, null, null))
                .when().put("/clientes/{id}", cliente.id())
                .then().statusCode(200);

        PageResponse<HistorialClienteResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/clientes/{clienteId}/historial?size=50", cliente.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(HistorialClienteResponse::tipoEvento)
                .contains(TipoEventoCliente.EDICION);
    }

    @Test
    void listarHistorial_incluyeEventoCambioRutaAlAsignarRuta() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        RutaResponse ruta = ClienteTestHelper.crearRutaActiva(admin.accessToken());

        AuthTestHelper.autenticado(admin.accessToken())
                .body(new AsignarRutaRequest(ruta.id()))
                .when().patch("/clientes/{id}/ruta", cliente.id())
                .then().statusCode(200);

        PageResponse<HistorialClienteResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/clientes/{clienteId}/historial?size=50", cliente.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(HistorialClienteResponse::tipoEvento)
                .contains(TipoEventoCliente.CAMBIO_RUTA);
    }

    @Test
    void listarHistorial_incluyeEventosActivacionYDesactivacion() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/clientes/{id}/estado", cliente.id())
                .then().statusCode(200);
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(true))
                .when().patch("/clientes/{id}/estado", cliente.id())
                .then().statusCode(200);

        PageResponse<HistorialClienteResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/clientes/{clienteId}/historial?size=50", cliente.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(HistorialClienteResponse::tipoEvento)
                .contains(TipoEventoCliente.DESACTIVACION, TipoEventoCliente.ACTIVACION);
    }

    @Test
    void listarHistorial_clienteInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/clientes/999999999/historial")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }
}
