package com.eldiamante360.insumoquimico.presentation;

import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.insumoquimico.domain.model.TipoMovimientoInsumo;
import com.eldiamante360.insumoquimico.presentation.InsumoQuimicoTestHelper.InsumoConLote;
import com.eldiamante360.insumoquimico.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.request.RegistrarEntradaInsumoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.request.RegistrarSalidaInsumoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.response.InsumoQuimicoResponse;
import com.eldiamante360.insumoquimico.presentation.dto.response.MovimientoInsumoResponse;
import com.eldiamante360.shared.it.ApiErrorAssertions;
import com.eldiamante360.shared.it.AuthTestHelper;
import com.eldiamante360.shared.it.BaseIntegrationTest;
import com.eldiamante360.shared.presentation.ApiErrorResponse;
import com.eldiamante360.shared.presentation.PageResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integracion de {@link MovimientoInsumoController} (entradas,
 * salidas y consulta de movimientos por insumo).
 *
 * <p>Al igual que en {@link InsumoQuimicoControllerIT}, VENDEDOR no tiene
 * ningun permiso INSUMO_* sembrado, por lo que recibe 403 en todos los
 * endpoints de este controlador.
 */
class MovimientoInsumoControllerIT extends BaseIntegrationTest {

    // ---------------------------------------------------------------
    // POST /insumos-quimicos/{insumoId}/entradas
    // ---------------------------------------------------------------

    @Test
    void registrarEntrada_datosValidos_devuelve201YActualizaStock() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());

        var request = new RegistrarEntradaInsumoRequest("LOTE-001", LocalDate.now().plusDays(90),
                new BigDecimal("25"), "Compra inicial de prueba IT");

        MovimientoInsumoResponse movimiento = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/entradas", insumo.id())
                .then().statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(MovimientoInsumoResponse.class);

        assertThat(movimiento.id()).isNotNull();
        assertThat(movimiento.insumoId()).isEqualTo(insumo.id());
        assertThat(movimiento.loteId()).isNotNull();
        assertThat(movimiento.tipoMovimiento()).isEqualTo(TipoMovimientoInsumo.ENTRADA);
        assertThat(movimiento.cantidad()).isEqualByComparingTo("25");
        assertThat(movimiento.stockResultante()).isEqualByComparingTo("25");
        assertThat(movimiento.motivo()).isEqualTo(request.motivo());
        assertThat(movimiento.usuarioId()).isNotNull();

        InsumoQuimicoResponse actualizado = InsumoQuimicoTestHelper.obtener(admin.accessToken(), insumo.id());
        assertThat(actualizado.stockActual()).isEqualByComparingTo("25");
    }

    @Test
    void registrarEntrada_sinNumeroLoteNiFechaVencimiento_devuelve201() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());

        var request = new RegistrarEntradaInsumoRequest(null, null, new BigDecimal("10"), "Entrada sin lote numerado");

        MovimientoInsumoResponse movimiento = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/entradas", insumo.id())
                .then().statusCode(201)
                .extract().as(MovimientoInsumoResponse.class);

        assertThat(movimiento.loteId()).isNotNull();
    }

    @Test
    void registrarEntrada_insumoInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new RegistrarEntradaInsumoRequest(null, null, BigDecimal.TEN, "Entrada IT");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/999999999/entradas")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void registrarEntrada_insumoInactivo_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/insumos-quimicos/{id}/estado", insumo.id())
                .then().statusCode(200);

        var request = new RegistrarEntradaInsumoRequest(null, null, BigDecimal.TEN, "Entrada sobre insumo inactivo");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/entradas", insumo.id())
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).contains(insumo.nombre());
    }

    @Test
    void registrarEntrada_cantidadCero_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        var request = new RegistrarEntradaInsumoRequest(null, null, BigDecimal.ZERO, "Cantidad invalida");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/entradas", insumo.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "cantidad");
    }

    @Test
    void registrarEntrada_cantidadNegativa_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        var request = new RegistrarEntradaInsumoRequest(null, null, new BigDecimal("-5"), "Cantidad invalida");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/entradas", insumo.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "cantidad");
    }

    @Test
    void registrarEntrada_motivoVacio_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        var request = new RegistrarEntradaInsumoRequest(null, null, BigDecimal.TEN, "");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/entradas", insumo.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "motivo");
    }

    @Test
    void registrarEntrada_motivoExcedeLongitudMaxima_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        var request = new RegistrarEntradaInsumoRequest(null, null, BigDecimal.TEN, "M".repeat(201));

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/entradas", insumo.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "motivo");
    }

    @Test
    void registrarEntrada_numeroLoteExcedeLongitudMaxima_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        var request = new RegistrarEntradaInsumoRequest("L".repeat(61), null, BigDecimal.TEN, "Motivo valido");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/entradas", insumo.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "numeroLote");
    }

    @Test
    void registrarEntrada_sinAutenticacion_devuelve401() {
        var request = new RegistrarEntradaInsumoRequest(null, null, BigDecimal.TEN, "Entrada sin auth");

        given().contentType(ContentType.JSON).body(request)
                .when().post("/insumos-quimicos/1/entradas")
                .then().statusCode(401);
    }

    @Test
    void registrarEntrada_comoVendedorSinNingunPermisoInsumo_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        var request = new RegistrarEntradaInsumoRequest(null, null, BigDecimal.TEN, "Entrada vendedor");
        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/entradas", insumo.id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // POST /insumos-quimicos/{insumoId}/salidas
    // ---------------------------------------------------------------

    @Test
    void registrarSalida_datosValidos_devuelve201YReduceStock() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoConLote insumoConLote = InsumoQuimicoTestHelper.crearInsumoConLote(admin.accessToken(), new BigDecimal("100"));

        var request = new RegistrarSalidaInsumoRequest(insumoConLote.loteId(), new BigDecimal("30"), "Uso en produccion");

        MovimientoInsumoResponse movimiento = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/salidas", insumoConLote.insumo().id())
                .then().statusCode(201)
                .extract().as(MovimientoInsumoResponse.class);

        assertThat(movimiento.tipoMovimiento()).isEqualTo(TipoMovimientoInsumo.SALIDA);
        assertThat(movimiento.loteId()).isEqualTo(insumoConLote.loteId());
        assertThat(movimiento.cantidad()).isEqualByComparingTo("30");
        assertThat(movimiento.stockResultante()).isEqualByComparingTo("70");

        InsumoQuimicoResponse actualizado = InsumoQuimicoTestHelper.obtener(admin.accessToken(), insumoConLote.insumo().id());
        assertThat(actualizado.stockActual()).isEqualByComparingTo("70");
    }

    @Test
    void registrarSalida_insumoInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new RegistrarSalidaInsumoRequest(1L, BigDecimal.TEN, "Salida IT");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/999999999/salidas")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void registrarSalida_insumoInactivo_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoConLote insumoConLote = InsumoQuimicoTestHelper.crearInsumoConLote(admin.accessToken(), new BigDecimal("100"));
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/insumos-quimicos/{id}/estado", insumoConLote.insumo().id())
                .then().statusCode(200);

        var request = new RegistrarSalidaInsumoRequest(insumoConLote.loteId(), BigDecimal.TEN, "Salida sobre insumo inactivo");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/salidas", insumoConLote.insumo().id())
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).contains(insumoConLote.insumo().nombre());
    }

    @Test
    void registrarSalida_loteInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());

        var request = new RegistrarSalidaInsumoRequest(999999999L, BigDecimal.TEN, "Salida con lote inexistente");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/salidas", insumo.id())
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void registrarSalida_loteDeOtroInsumo_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoConLote insumoA = InsumoQuimicoTestHelper.crearInsumoConLote(admin.accessToken(), new BigDecimal("100"));
        InsumoQuimicoResponse insumoB = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());

        // El lote pertenece a insumoA, se intenta usar en una salida de insumoB.
        var request = new RegistrarSalidaInsumoRequest(insumoA.loteId(), BigDecimal.TEN, "Salida con lote de otro insumo");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/salidas", insumoB.id())
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void registrarSalida_loteVencido_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoConLote insumoConLoteVencido = InsumoQuimicoTestHelper.crearInsumoConLoteVencido(admin.accessToken(), new BigDecimal("50"));

        var request = new RegistrarSalidaInsumoRequest(insumoConLoteVencido.loteId(), BigDecimal.TEN, "Salida de lote vencido");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/salidas", insumoConLoteVencido.insumo().id())
                .then().statusCode(422);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
    }

    @Test
    void registrarSalida_cantidadMayorQueLaDisponibleEnElLote_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoConLote insumoConLote = InsumoQuimicoTestHelper.crearInsumoConLote(admin.accessToken(), new BigDecimal("20"));

        var request = new RegistrarSalidaInsumoRequest(insumoConLote.loteId(), new BigDecimal("21"), "Salida mayor que el stock del lote");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/salidas", insumoConLote.insumo().id())
                .then().statusCode(422);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
    }

    @Test
    void registrarSalida_cantidadCero_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoConLote insumoConLote = InsumoQuimicoTestHelper.crearInsumoConLote(admin.accessToken(), new BigDecimal("20"));

        var request = new RegistrarSalidaInsumoRequest(insumoConLote.loteId(), BigDecimal.ZERO, "Cantidad invalida");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/salidas", insumoConLote.insumo().id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "cantidad");
    }

    @Test
    void registrarSalida_loteIdNulo_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());

        var request = new RegistrarSalidaInsumoRequest(null, BigDecimal.TEN, "Sin lote");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/salidas", insumo.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "loteId");
    }

    @Test
    void registrarSalida_motivoVacio_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoConLote insumoConLote = InsumoQuimicoTestHelper.crearInsumoConLote(admin.accessToken(), new BigDecimal("20"));

        var request = new RegistrarSalidaInsumoRequest(insumoConLote.loteId(), BigDecimal.TEN, "");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/salidas", insumoConLote.insumo().id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "motivo");
    }

    @Test
    void registrarSalida_sinAutenticacion_devuelve401() {
        var request = new RegistrarSalidaInsumoRequest(1L, BigDecimal.TEN, "Salida sin auth");

        given().contentType(ContentType.JSON).body(request)
                .when().post("/insumos-quimicos/1/salidas")
                .then().statusCode(401);
    }

    @Test
    void registrarSalida_comoVendedorSinNingunPermisoInsumo_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoConLote insumoConLote = InsumoQuimicoTestHelper.crearInsumoConLote(admin.accessToken(), new BigDecimal("20"));
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        var request = new RegistrarSalidaInsumoRequest(insumoConLote.loteId(), BigDecimal.TEN, "Salida vendedor");
        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/salidas", insumoConLote.insumo().id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // GET /insumos-quimicos/{insumoId}/movimientos
    // ---------------------------------------------------------------

    @Test
    void listarMovimientos_devuelvePaginaConLosMovimientosDelInsumo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoConLote insumoConLote = InsumoQuimicoTestHelper.crearInsumoConLote(admin.accessToken(), new BigDecimal("40"));
        MovimientoInsumoResponse salida = AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarSalidaInsumoRequest(insumoConLote.loteId(), new BigDecimal("15"), "Salida para listado"))
                .when().post("/insumos-quimicos/{insumoId}/salidas", insumoConLote.insumo().id())
                .then().statusCode(201)
                .extract().as(MovimientoInsumoResponse.class);

        PageResponse<MovimientoInsumoResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/insumos-quimicos/{insumoId}/movimientos", insumoConLote.insumo().id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(MovimientoInsumoResponse::id)
                .contains(salida.id());
        assertThat(pagina.contenido())
                .allSatisfy(m -> assertThat(m.insumoId()).isEqualTo(insumoConLote.insumo().id()));
    }

    @Test
    void listarMovimientos_sinAutenticacion_devuelve401() {
        given().when().get("/insumos-quimicos/1/movimientos")
                .then().statusCode(401);
    }

    @Test
    void listarMovimientos_comoVendedorSinPermisoInsumoLeer_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        InsumoQuimicoResponse insumo = InsumoQuimicoTestHelper.crearInsumoActivo(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/insumos-quimicos/{insumoId}/movimientos", insumo.id())
                .then().statusCode(403);
    }
}
