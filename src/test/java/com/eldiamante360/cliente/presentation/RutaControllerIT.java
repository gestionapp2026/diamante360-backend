package com.eldiamante360.cliente.presentation;

import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.cliente.presentation.dto.request.ActualizarRutaRequest;
import com.eldiamante360.cliente.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.cliente.presentation.dto.request.CrearRutaRequest;
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

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integracion de {@link RutaController}.
 */
class RutaControllerIT extends BaseIntegrationTest {

    // ---------------------------------------------------------------
    // POST /rutas
    // ---------------------------------------------------------------

    @Test
    void crear_datosValidos_devuelve201ConRutaActiva() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearRutaRequest(TestDataFactory.nombreCompleto("Ruta Norte"), "Zona norte de la ciudad");

        RutaResponse creada = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/rutas")
                .then().statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(RutaResponse.class);

        assertThat(creada.id()).isNotNull();
        assertThat(creada.nombre()).isEqualTo(request.nombre());
        assertThat(creada.descripcion()).isEqualTo(request.descripcion());
        assertThat(creada.activo()).isTrue();
    }

    @Test
    void crear_sinDescripcion_devuelve201ConDescripcionNula() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearRutaRequest(TestDataFactory.nombreCompleto("Ruta Sin Desc"), null);

        RutaResponse creada = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/rutas")
                .then().statusCode(201)
                .extract().as(RutaResponse.class);

        assertThat(creada.descripcion()).isNull();
    }

    @Test
    void crear_nombreDuplicado_devuelve409() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse existente = ClienteTestHelper.crearRutaActiva(admin.accessToken());

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearRutaRequest(existente.nombre(), "Otra descripcion"))
                .when().post("/rutas")
                .then().statusCode(409);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 409);
        assertThat(error.message()).contains(existente.nombre());
    }

    @Test
    void crear_nombreVacio_devuelve400() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearRutaRequest("", null))
                .when().post("/rutas")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombre");
    }

    @Test
    void crear_sinAutenticacion_devuelve401() {
        given().contentType(ContentType.JSON)
                .body(new CrearRutaRequest(TestDataFactory.nombreCompleto("Sin Auth"), null))
                .when().post("/rutas")
                .then().statusCode(401);
    }

    @Test
    void crear_comoVendedorConPermisoClienteCrear_devuelve201() {
        // VENDEDOR tiene CLIENTE_CREAR (V21__permisos_vendedor_crear_cliente_producto.sql), que este
        // endpoint reutiliza para crear rutas.
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new CrearRutaRequest(TestDataFactory.nombreCompleto("Vendedor Ruta"), null))
                .when().post("/rutas")
                .then().statusCode(201);
    }

    // ---------------------------------------------------------------
    // GET /rutas
    // ---------------------------------------------------------------

    @Test
    void listar_comoAdmin_devuelveListaSinPaginarConLaRutaCreada() {
        // A diferencia de /clientes, /rutas NO esta paginado (devuelve List<RutaResponse>
        // directo). Ver RutaController.listar() -- inconsistente con el resto de endpoints
        // de listado del API mismo, pero es el comportamiento actual documentado.
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse creada = ClienteTestHelper.crearRutaActiva(admin.accessToken());

        List<RutaResponse> rutas = List.of(
                AuthTestHelper.autenticado(admin.accessToken())
                        .when().get("/rutas")
                        .then().statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract().as(RutaResponse[].class));

        assertThat(rutas).extracting(RutaResponse::id).contains(creada.id());
    }

    @Test
    void listar_comoVendedorConPermisoClienteLeer_devuelve200() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/rutas")
                .then().statusCode(200);
    }

    // ---------------------------------------------------------------
    // GET /rutas/{id}
    // ---------------------------------------------------------------

    @Test
    void obtener_idExistente_devuelveLaRutaCreada() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse creada = ClienteTestHelper.crearRutaActiva(admin.accessToken());

        RutaResponse obtenida = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/rutas/{id}", creada.id())
                .then().statusCode(200)
                .extract().as(RutaResponse.class);

        assertThat(obtenida).isEqualTo(creada);
    }

    @Test
    void obtener_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/rutas/999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    // ---------------------------------------------------------------
    // PUT /rutas/{id}
    // ---------------------------------------------------------------

    @Test
    void actualizar_datosValidos_persisteNombreYDescripcion() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse creada = ClienteTestHelper.crearRutaActiva(admin.accessToken());
        var request = new ActualizarRutaRequest(TestDataFactory.nombreCompleto("Ruta Renombrada"), "Nueva descripcion");

        RutaResponse actualizada = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/rutas/{id}", creada.id())
                .then().statusCode(200)
                .extract().as(RutaResponse.class);

        assertThat(actualizada.nombre()).isEqualTo(request.nombre());
        assertThat(actualizada.descripcion()).isEqualTo(request.descripcion());
    }

    @Test
    void actualizar_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new ActualizarRutaRequest(TestDataFactory.nombreCompleto("No Existe"), null))
                .when().put("/rutas/999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void actualizar_nombreVacio_devuelve400() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse creada = ClienteTestHelper.crearRutaActiva(admin.accessToken());

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new ActualizarRutaRequest("", null))
                .when().put("/rutas/{id}", creada.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombre");
    }

    @Test
    void actualizar_comoVendedorSinPermisoClienteEditar_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse creada = ClienteTestHelper.crearRutaActiva(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new ActualizarRutaRequest(TestDataFactory.nombreCompleto("Intento Vendedor"), null))
                .when().put("/rutas/{id}", creada.id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // PATCH /rutas/{id}/estado
    // ---------------------------------------------------------------

    @Test
    void cambiarEstado_desactivarYReactivar_reflejaElCambio() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse creada = ClienteTestHelper.crearRutaActiva(admin.accessToken());

        RutaResponse desactivada = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/rutas/{id}/estado", creada.id())
                .then().statusCode(200)
                .extract().as(RutaResponse.class);
        assertThat(desactivada.activo()).isFalse();

        RutaResponse reactivada = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(true))
                .when().patch("/rutas/{id}/estado", creada.id())
                .then().statusCode(200)
                .extract().as(RutaResponse.class);
        assertThat(reactivada.activo()).isTrue();
    }

    @Test
    void cambiarEstado_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/rutas/999999999/estado")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void cambiarEstado_comoVendedorSinPermisoClienteEliminar_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse creada = ClienteTestHelper.crearRutaActiva(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/rutas/{id}/estado", creada.id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // GET /rutas/{id}/clientes
    // ---------------------------------------------------------------

    @Test
    void listarClientes_conRutaExistente_devuelvePaginaDeClientesAsignados() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        RutaResponse ruta = ClienteTestHelper.crearRutaActiva(admin.accessToken());
        ClienteResponse cliente = ClienteTestHelper.crearCliente(admin.accessToken(), ruta.id());

        PageResponse<ClienteResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/rutas/{id}/clientes", ruta.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido()).extracting(ClienteResponse::id).contains(cliente.id());
    }

    @Test
    void listarClientes_conRutaInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/rutas/999999999/clientes")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }
}
