package com.eldiamante360.insumoquimico.presentation;

import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import com.eldiamante360.insumoquimico.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.request.CrearInsumoQuimicoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.response.InsumoQuimicoResponse;
import com.eldiamante360.insumoquimico.presentation.dto.response.LoteResponse;
import com.eldiamante360.shared.it.ApiErrorAssertions;
import com.eldiamante360.shared.it.AuthTestHelper;
import com.eldiamante360.shared.it.BaseIntegrationTest;
import com.eldiamante360.shared.it.TestDataFactory;
import com.eldiamante360.shared.presentation.ApiErrorResponse;
import com.eldiamante360.shared.presentation.PageResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integracion de {@link InsumoQuimicoController}.
 *
 * <p><b>Nota sobre permisos:</b> a diferencia del modulo Productos y
 * Categorias (donde VENDEDOR tiene PRODUCTO_LEER), el rol VENDEDOR sembrado
 * NO tiene ninguno de los permisos INSUMO_* (INSUMO_LEER, INSUMO_CREAR,
 * INSUMO_ELIMINAR, INSUMO_AJUSTAR; ver V1__create_seguridad.sql y
 * V3__create_insumo_quimico.sql), por lo que recibe 403 en absolutamente
 * todos los endpoints de este controlador, incluidos los de solo lectura.
 */
class InsumoQuimicoControllerIT extends BaseIntegrationTest {

    // ---------------------------------------------------------------
    // POST /insumos-quimicos
    // ---------------------------------------------------------------

    @Test
    void crear_datosValidos_devuelve201YCuerpo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearInsumoQuimicoRequest(TestDataFactory.nombreCompleto("Insumo IT"), UnidadMedidaInsumo.KG);

        InsumoQuimicoResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos")
                .then().statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(InsumoQuimicoResponse.class);

        assertThat(creado.id()).isNotNull();
        assertThat(creado.nombre()).isEqualTo(request.nombre());
        assertThat(creado.unidadMedida()).isEqualTo(UnidadMedidaInsumo.KG);
        assertThat(creado.stockActual()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(creado.activo()).isTrue();
    }

    @Test
    void crear_nombreDuplicado_devuelve409() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse existente = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());

        var request = new CrearInsumoQuimicoRequest(existente.nombre(), UnidadMedidaInsumo.LT);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos")
                .then().statusCode(409);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 409);
        assertThat(error.message()).contains(existente.nombre());
    }

    @Test
    void crear_nombreVacio_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearInsumoQuimicoRequest("", UnidadMedidaInsumo.UND);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombre");
    }

    @Test
    void crear_nombreExcedeLongitudMaxima_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearInsumoQuimicoRequest("N".repeat(121), UnidadMedidaInsumo.UND);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombre");
    }

    @Test
    void crear_unidadMedidaNula_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        String json = "{\"nombre\":\"" + TestDataFactory.nombreCompleto("Insumo Sin Unidad") + "\",\"unidadMedida\":null}";

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(json)
                .when().post("/insumos-quimicos")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "unidadMedida");
    }

    @Test
    void crear_sinAutenticacion_devuelve401() {
        var request = new CrearInsumoQuimicoRequest(TestDataFactory.nombreCompleto("Insumo Sin Auth"), UnidadMedidaInsumo.UND);

        given().contentType(ContentType.JSON).body(request)
                .when().post("/insumos-quimicos")
                .then().statusCode(401);
    }

    @Test
    void crear_comoVendedorSinNingunPermisoInsumo_devuelve403() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");
        var request = new CrearInsumoQuimicoRequest(TestDataFactory.nombreCompleto("Insumo Vendedor"), UnidadMedidaInsumo.UND);

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(request)
                .when().post("/insumos-quimicos")
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // GET /insumos-quimicos
    // ---------------------------------------------------------------

    @Test
    void listar_devuelvePaginaConFormaEstandarYElInsumoCreado() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse creado = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());

        PageResponse<InsumoQuimicoResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/insumos-quimicos?size=200")
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(InsumoQuimicoResponse::id)
                .contains(creado.id());
    }

    @Test
    void listar_sinAutenticacion_devuelve401() {
        given().when().get("/insumos-quimicos")
                .then().statusCode(401);
    }

    @Test
    void listar_comoVendedorSinPermisoInsumoLeer_devuelve403() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/insumos-quimicos")
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // GET /insumos-quimicos/vencimientos
    // ---------------------------------------------------------------

    @Test
    void vencimientos_incluyeLoteDentroDelUmbralYExcluyeFueraDelUmbral() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());

        var entradaCercana = InsumoQuimicoTestHelper.registrarEntrada(admin.accessToken(), insumo.id(),
                "LOTE-CERCA-" + TestDataFactory.sufijoUnico(), java.time.LocalDate.now().plusDays(3),
                BigDecimal.TEN, "Entrada IT vencimiento cercano");
        var entradaLejana = InsumoQuimicoTestHelper.registrarEntrada(admin.accessToken(), insumo.id(),
                "LOTE-LEJOS-" + TestDataFactory.sufijoUnico(), java.time.LocalDate.now().plusDays(200),
                BigDecimal.TEN, "Entrada IT vencimiento lejano");

        List<LoteResponse> porVencer = List.of(AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/insumos-quimicos/vencimientos?dias=10")
                .then().statusCode(200)
                .extract().as(LoteResponse[].class));

        assertThat(porVencer).extracting(LoteResponse::id).contains(entradaCercana.loteId());
        assertThat(porVencer).extracting(LoteResponse::id).doesNotContain(entradaLejana.loteId());
    }

    @Test
    void vencimientos_sinAutenticacion_devuelve401() {
        given().when().get("/insumos-quimicos/vencimientos")
                .then().statusCode(401);
    }

    @Test
    void vencimientos_comoVendedorSinPermisoInsumoLeer_devuelve403() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/insumos-quimicos/vencimientos")
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // GET /insumos-quimicos/{id}
    // ---------------------------------------------------------------

    @Test
    void obtener_idExistente_devuelveElInsumoCreado() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse creado = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());

        InsumoQuimicoResponse obtenido = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/insumos-quimicos/{id}", creado.id())
                .then().statusCode(200)
                .extract().as(InsumoQuimicoResponse.class);

        assertThat(obtenido.id()).isEqualTo(creado.id());
        assertThat(obtenido.nombre()).isEqualTo(creado.nombre());
    }

    @Test
    void obtener_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/insumos-quimicos/999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void obtener_sinAutenticacion_devuelve401() {
        given().when().get("/insumos-quimicos/1")
                .then().statusCode(401);
    }

    @Test
    void obtener_comoVendedorSinPermisoInsumoLeer_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse creado = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/insumos-quimicos/{id}", creado.id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // GET /insumos-quimicos/{id}/lotes
    // ---------------------------------------------------------------

    @Test
    void listarLotes_devuelvePaginaConLosLotesDelInsumo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var insumoConLote = InsumoQuimicoTestHelper.crearInsumoConLote(admin.accessToken(), new BigDecimal("50"));

        PageResponse<LoteResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/insumos-quimicos/{id}/lotes", insumoConLote.insumo().id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(LoteResponse::id)
                .contains(insumoConLote.loteId());
        assertThat(pagina.contenido())
                .allSatisfy(l -> assertThat(l.insumoId()).isEqualTo(insumoConLote.insumo().id()));
    }

    /**
     * Nota (no es un bug): {@code ListarLotesPorInsumoService} no valida que
     * el insumo exista antes de consultar sus lotes (a diferencia de
     * {@code obtener}/{@code cambiarEstado}), simplemente filtra por
     * insumoId. Un id inexistente devuelve una pagina vacia con 200 en vez
     * de 404. Se documenta el comportamiento real.
     */
    @Test
    void listarLotes_insumoInexistente_devuelvePaginaVacia() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        PageResponse<LoteResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/insumos-quimicos/999999999/lotes")
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido()).isEmpty();
    }

    @Test
    void listarLotes_sinAutenticacion_devuelve401() {
        given().when().get("/insumos-quimicos/1/lotes")
                .then().statusCode(401);
    }

    @Test
    void listarLotes_comoVendedorSinPermisoInsumoLeer_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse creado = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/insumos-quimicos/{id}/lotes", creado.id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // PATCH /insumos-quimicos/{id}/estado
    // ---------------------------------------------------------------

    @Test
    void cambiarEstado_desactivar_devuelve200ConActivoFalse() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse creado = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());

        InsumoQuimicoResponse actualizado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/insumos-quimicos/{id}/estado", creado.id())
                .then().statusCode(200)
                .extract().as(InsumoQuimicoResponse.class);

        assertThat(actualizado.activo()).isFalse();
    }

    @Test
    void cambiarEstado_reactivar_devuelve200ConActivoTrue() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse creado = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/insumos-quimicos/{id}/estado", creado.id())
                .then().statusCode(200);

        InsumoQuimicoResponse actualizado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(true))
                .when().patch("/insumos-quimicos/{id}/estado", creado.id())
                .then().statusCode(200)
                .extract().as(InsumoQuimicoResponse.class);

        assertThat(actualizado.activo()).isTrue();
    }

    @Test
    void cambiarEstado_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/insumos-quimicos/999999999/estado")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void cambiarEstado_activoNulo_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse creado = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(null))
                .when().patch("/insumos-quimicos/{id}/estado", creado.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "activo");
    }

    @Test
    void cambiarEstado_sinAutenticacion_devuelve401() {
        given().contentType(ContentType.JSON).body(new CambiarEstadoRequest(false))
                .when().patch("/insumos-quimicos/1/estado")
                .then().statusCode(401);
    }

    @Test
    void cambiarEstado_comoVendedorSinNingunPermisoInsumo_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse creado = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/insumos-quimicos/{id}/estado", creado.id())
                .then().statusCode(403);
    }
}
