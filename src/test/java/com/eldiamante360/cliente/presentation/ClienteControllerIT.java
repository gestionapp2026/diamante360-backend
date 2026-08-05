package com.eldiamante360.cliente.presentation;

import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
import com.eldiamante360.cliente.presentation.dto.request.ActualizarClienteRequest;
import com.eldiamante360.cliente.presentation.dto.request.AsignarRutaRequest;
import com.eldiamante360.cliente.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.cliente.presentation.dto.request.CrearClienteRequest;
import com.eldiamante360.cliente.presentation.dto.response.ClienteResponse;
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

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integracion de {@link ClienteController}.
 */
class ClienteControllerIT extends BaseIntegrationTest {

    // ---------------------------------------------------------------
    // POST /clientes
    // ---------------------------------------------------------------

    @Test
    void crear_datosValidos_devuelve201ConClienteActivoYRutaAsignada() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse ruta = ClienteTestHelper.crearRutaActiva(admin.accessToken());

        var request = new CrearClienteRequest(TipoDocumentoCliente.CC, TestDataFactory.numeroDocumento(),
                TestDataFactory.nombreCompleto("Cliente Full"), TestDataFactory.telefono(),
                TestDataFactory.email("full"), "Calle 1 # 2-3", ruta.id());

        ClienteResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/clientes")
                .then().statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(ClienteResponse.class);

        assertThat(creado.id()).isNotNull();
        assertThat(creado.tipoDocumento()).isEqualTo(TipoDocumentoCliente.CC);
        assertThat(creado.numeroDocumento()).isEqualTo(request.numeroDocumento());
        assertThat(creado.nombre()).isEqualTo(request.nombre());
        assertThat(creado.rutaId()).isEqualTo(ruta.id());
        assertThat(creado.rutaNombre()).isEqualTo(ruta.nombre());
        assertThat(creado.activo()).isTrue();
    }

    @Test
    void crear_soloConCamposObligatorios_devuelve201ConOpcionalesEnNull() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        assertThat(creado.telefono()).isNull();
        assertThat(creado.email()).isNull();
        assertThat(creado.direccion()).isNull();
        assertThat(creado.rutaId()).isNull();
        assertThat(creado.rutaNombre()).isNull();
        assertThat(creado.activo()).isTrue();
    }

    @Test
    void crear_numeroDocumentoDuplicado_devuelve409() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse existente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var request = new CrearClienteRequest(TipoDocumentoCliente.CC, existente.numeroDocumento(),
                TestDataFactory.nombreCompleto("Otro Cliente"), null, null, null, null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/clientes")
                .then().statusCode(409);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 409);
        assertThat(error.message()).contains(existente.numeroDocumento());
    }

    @Test
    void crear_rutaInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearClienteRequest(TipoDocumentoCliente.CC, TestDataFactory.numeroDocumento(),
                TestDataFactory.nombreCompleto("Cliente Ruta Fake"), null, null, null, 999_999_999L);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/clientes")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void crear_rutaInactiva_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse ruta = ClienteTestHelper.crearRutaActiva(admin.accessToken());
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/rutas/{id}/estado", ruta.id())
                .then().statusCode(200);

        var request = new CrearClienteRequest(TipoDocumentoCliente.CC, TestDataFactory.numeroDocumento(),
                TestDataFactory.nombreCompleto("Cliente Ruta Inactiva"), null, null, null, ruta.id());

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/clientes")
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).contains(ruta.nombre());
    }

    @Test
    void crear_camposObligatoriosFaltantesYEmailInvalido_devuelve400ConUnErrorPorCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearClienteRequest(null, "", "", null, "no-es-un-email", null, null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/clientes")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "tipoDocumento");
        ApiErrorAssertions.verificarErrorDeCampo(error, "numeroDocumento");
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombre");
        ApiErrorAssertions.verificarErrorDeCampo(error, "email");
    }

    @Test
    void crear_sinAutenticacion_devuelve401() {
        var request = new CrearClienteRequest(TipoDocumentoCliente.CC, TestDataFactory.numeroDocumento(),
                TestDataFactory.nombreCompleto("Sin Auth"), null, null, null, null);

        given().contentType(ContentType.JSON).body(request)
                .when().post("/clientes")
                .then().statusCode(401);
    }

    @Test
    void crear_comoVendedorSinPermisoClienteCrear_devuelve403() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");
        var request = new CrearClienteRequest(TipoDocumentoCliente.CC, TestDataFactory.numeroDocumento(),
                TestDataFactory.nombreCompleto("Vendedor Sin Permiso"), null, null, null, null);

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(request)
                .when().post("/clientes")
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // GET /clientes
    // ---------------------------------------------------------------

    @Test
    void listar_comoAdmin_devuelvePaginaConFormaEstandar() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        PageResponse<ClienteResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/clientes?page=0&size=5")
                .then().statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.tamano()).isEqualTo(5);
        assertThat(pagina.pagina()).isZero();
        assertThat(pagina.totalElementos()).isGreaterThanOrEqualTo(1);
        assertThat(pagina.contenido()).isNotEmpty();
    }

    @Test
    void listar_conTexto_filtraPorNombreOnumeroDocumento() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        PageResponse<ClienteResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/clientes?texto=" + creado.numeroDocumento())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido()).extracting(ClienteResponse::id).contains(creado.id());
    }

    @Test
    void listar_comoVendedorConPermisoClienteLeer_devuelve200() {
        // A diferencia de otros modulos, ambos roles sembrados (ADMIN y VENDEDOR)
        // tienen CLIENTE_LEER, por lo que no hay forma de probar un 403 en un
        // endpoint de solo lectura con los datos sembrados actuales; se verifica
        // en cambio el acceso permitido explicitamente.
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/clientes")
                .then().statusCode(200);
    }

    // ---------------------------------------------------------------
    // GET /clientes/{id}
    // ---------------------------------------------------------------

    @Test
    void obtener_idExistente_devuelveElClienteCreado() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        ClienteResponse obtenido = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/clientes/{id}", creado.id())
                .then().statusCode(200)
                .extract().as(ClienteResponse.class);

        assertThat(obtenido).isEqualTo(creado);
    }

    @Test
    void obtener_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/clientes/999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    // ---------------------------------------------------------------
    // PUT /clientes/{id}
    // ---------------------------------------------------------------

    @Test
    void actualizar_datosValidos_persisteLosNuevosDatos() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var request = new ActualizarClienteRequest(TestDataFactory.nombreCompleto("Cliente Actualizado"),
                TestDataFactory.telefono(), TestDataFactory.email("actualizado"), "Nueva Direccion 456");

        ClienteResponse actualizado = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/clientes/{id}", creado.id())
                .then().statusCode(200)
                .extract().as(ClienteResponse.class);

        assertThat(actualizado.nombre()).isEqualTo(request.nombre());
        assertThat(actualizado.telefono()).isEqualTo(request.telefono());
        assertThat(actualizado.email()).isEqualTo(request.email());
        assertThat(actualizado.direccion()).isEqualTo(request.direccion());
        assertThat(actualizado.numeroDocumento()).isEqualTo(creado.numeroDocumento());
    }

    @Test
    void actualizar_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new ActualizarClienteRequest(TestDataFactory.nombreCompleto("No Existe"), null, null, null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/clientes/999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void actualizar_nombreVacio_devuelve400() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        var request = new ActualizarClienteRequest("", null, "no-es-email", null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/clientes/{id}", creado.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombre");
        ApiErrorAssertions.verificarErrorDeCampo(error, "email");
    }

    @Test
    void actualizar_comoVendedorSinPermisoClienteEditar_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        var request = new ActualizarClienteRequest(TestDataFactory.nombreCompleto("Intento Vendedor"), null, null, null);
        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(request)
                .when().put("/clientes/{id}", creado.id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // PATCH /clientes/{id}/estado
    // ---------------------------------------------------------------

    @Test
    void cambiarEstado_desactivarYReactivar_reflejaElCambio() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        ClienteResponse desactivado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/clientes/{id}/estado", creado.id())
                .then().statusCode(200)
                .extract().as(ClienteResponse.class);
        assertThat(desactivado.activo()).isFalse();

        ClienteResponse reactivado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(true))
                .when().patch("/clientes/{id}/estado", creado.id())
                .then().statusCode(200)
                .extract().as(ClienteResponse.class);
        assertThat(reactivado.activo()).isTrue();
    }

    @Test
    void cambiarEstado_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/clientes/999999999/estado")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void cambiarEstado_campoActivoFaltante_devuelve400() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body("{}")
                .when().patch("/clientes/{id}/estado", creado.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "activo");
    }

    @Test
    void cambiarEstado_comoVendedorSinPermisoClienteEliminar_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/clientes/{id}/estado", creado.id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // PATCH /clientes/{id}/ruta
    // ---------------------------------------------------------------

    @Test
    void asignarRuta_conRutaActiva_asignaLaRutaAlCliente() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        RutaResponse ruta = ClienteTestHelper.crearRutaActiva(admin.accessToken());

        ClienteResponse actualizado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new AsignarRutaRequest(ruta.id()))
                .when().patch("/clientes/{id}/ruta", creado.id())
                .then().statusCode(200)
                .extract().as(ClienteResponse.class);

        assertThat(actualizado.rutaId()).isEqualTo(ruta.id());
        assertThat(actualizado.rutaNombre()).isEqualTo(ruta.nombre());
    }

    @Test
    void asignarRuta_conRutaIdNulo_quitaLaRutaAsignada() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse ruta = ClienteTestHelper.crearRutaActiva(admin.accessToken());
        ClienteResponse creado = ClienteTestHelper.crearCliente(admin.accessToken(), ruta.id());
        assertThat(creado.rutaId()).isEqualTo(ruta.id());

        ClienteResponse sinRuta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new AsignarRutaRequest(null))
                .when().patch("/clientes/{id}/ruta", creado.id())
                .then().statusCode(200)
                .extract().as(ClienteResponse.class);

        assertThat(sinRuta.rutaId()).isNull();
        assertThat(sinRuta.rutaNombre()).isNull();
    }

    @Test
    void asignarRuta_conRutaInactiva_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        RutaResponse ruta = ClienteTestHelper.crearRutaActiva(admin.accessToken());
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/rutas/{id}/estado", ruta.id())
                .then().statusCode(200);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new AsignarRutaRequest(ruta.id()))
                .when().patch("/clientes/{id}/ruta", creado.id())
                .then().statusCode(422);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
    }

    @Test
    void asignarRuta_conRutaInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new AsignarRutaRequest(999999999L))
                .when().patch("/clientes/{id}/ruta", creado.id())
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void asignarRuta_clienteInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse ruta = ClienteTestHelper.crearRutaActiva(admin.accessToken());

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new AsignarRutaRequest(ruta.id()))
                .when().patch("/clientes/999999999/ruta")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void asignarRuta_comoVendedorSinPermisoClienteEditar_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse creado = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        RutaResponse ruta = ClienteTestHelper.crearRutaActiva(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new AsignarRutaRequest(ruta.id()))
                .when().patch("/clientes/{id}/ruta", creado.id())
                .then().statusCode(403);
    }
}
