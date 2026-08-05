package com.eldiamante360.deudor.presentation;

import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.cliente.presentation.ClienteTestHelper;
import com.eldiamante360.cliente.presentation.dto.response.ClienteResponse;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.TipoEventoCuentaPorCobrar;
import com.eldiamante360.deudor.presentation.dto.request.RegistrarAbonoRequest;
import com.eldiamante360.deudor.presentation.dto.response.AbonoResponse;
import com.eldiamante360.deudor.presentation.dto.response.CuentaPorCobrarResponse;
import com.eldiamante360.deudor.presentation.dto.response.HistorialCuentaPorCobrarResponse;
import com.eldiamante360.deudor.presentation.dto.response.SaldoClienteResponse;
import com.eldiamante360.factura.presentation.dto.response.FacturaResponse;
import com.eldiamante360.shared.domain.model.MedioPago;
import com.eldiamante360.shared.it.ApiErrorAssertions;
import com.eldiamante360.shared.it.AuthTestHelper;
import com.eldiamante360.shared.it.BaseIntegrationTest;
import com.eldiamante360.shared.presentation.ApiErrorResponse;
import com.eldiamante360.shared.presentation.PageResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integracion de {@link DeudorController}.
 *
 * <p><b>Nota sobre los datos de prueba:</b> este modulo no expone ningun
 * endpoint para crear una cuenta por cobrar directamente (solo se genera al
 * emitir una factura a credito, ver {@link DeudorTestHelper}). Aunque el
 * modulo Deudores se prueba antes que Productos/Categorias y Facturacion en
 * el orden acordado, el codigo de produccion de esos modulos ya existe, asi
 * que estas pruebas siembran sus datos recorriendo el flujo de negocio real
 * completo por HTTP (categoria -> producto -> cliente -> factura a credito),
 * nunca con atajos internos.
 *
 * <p><b>Nota sobre permisos:</b> este modulo solo define dos permisos,
 * DEUDOR_LEER y DEUDOR_ABONAR, y el rol VENDEDOR sembrado tiene AMBOS (ver
 * V1__create_seguridad.sql). Por lo tanto, con los roles sembrados
 * (ADMIN/VENDEDOR) no existe ningun escenario de permiso insuficiente (403)
 * que se pueda ejercitar en este controlador: todo usuario autenticado con
 * cualquiera de los dos roles sembrados puede usar los 7 endpoints. Se
 * documenta con pruebas positivas explicitas en vez de omitirse.
 */
class DeudorControllerIT extends BaseIntegrationTest {

    // ---------------------------------------------------------------
    // GET /deudores
    // ---------------------------------------------------------------

    @Test
    void listar_comoAdmin_devuelvePaginaConLaCuentaCreada() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("150000.00"));

        PageResponse<CuentaPorCobrarResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores?size=200")
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(CuentaPorCobrarResponse::id)
                .contains(cuenta.id());
    }

    @Test
    void listar_conClienteId_filtraPorCliente() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrarParaCliente(
                admin.accessToken(), cliente.id(), new BigDecimal("80000.00"));

        PageResponse<CuentaPorCobrarResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores?clienteId={clienteId}", cliente.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido()).hasSize(1);
        assertThat(pagina.contenido().get(0).id()).isEqualTo(cuenta.id());
        assertThat(pagina.contenido().get(0).clienteId()).isEqualTo(cliente.id());
    }

    @Test
    void listar_conClienteIdInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores?clienteId=999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void listar_conEstado_filtraPorEstado() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("50000.00"));

        PageResponse<CuentaPorCobrarResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores?estado=PENDIENTE&size=200")
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(CuentaPorCobrarResponse::id)
                .contains(cuenta.id());
        assertThat(pagina.contenido())
                .allSatisfy(c -> assertThat(c.estado()).isEqualTo(EstadoCuentaPorCobrar.PENDIENTE));
    }

    @Test
    void listar_comoVendedorConPermisoDeudorLeer_devuelve200() {
        // Documenta que el rol VENDEDOR sembrado tiene DEUDOR_LEER (ver clase).
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/deudores")
                .then().statusCode(200);
    }

    @Test
    void listar_sinAutenticacion_devuelve401() {
        given()
                .when().get("/deudores")
                .then().statusCode(401);
    }

    // ---------------------------------------------------------------
    // GET /deudores/{id}
    // ---------------------------------------------------------------

    @Test
    void obtener_idExistente_devuelveLaCuentaCreada() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("200000.00"));

        CuentaPorCobrarResponse obtenida = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/{id}", cuenta.id())
                .then().statusCode(200)
                .extract().as(CuentaPorCobrarResponse.class);

        assertThat(obtenida.id()).isEqualTo(cuenta.id());
        assertThat(obtenida.montoOriginal()).isEqualByComparingTo("200000.00");
        assertThat(obtenida.saldoPendiente()).isEqualByComparingTo("200000.00");
        assertThat(obtenida.estado()).isEqualTo(EstadoCuentaPorCobrar.PENDIENTE);
        assertThat(obtenida.facturaId()).isNotNull();
        assertThat(obtenida.fecha()).isNotNull();
    }

    @Test
    void obtener_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    // ---------------------------------------------------------------
    // GET /deudores/factura/{facturaId}
    // ---------------------------------------------------------------

    @Test
    void obtenerPorFactura_facturaIdExistente_devuelveLaCuenta() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        FacturaResponse factura = DeudorTestHelper.crearFacturaCreditoParaClienteNuevo(admin.accessToken(), new BigDecimal("90000.00"));

        CuentaPorCobrarResponse cuenta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/factura/{facturaId}", factura.id())
                .then().statusCode(200)
                .extract().as(CuentaPorCobrarResponse.class);

        assertThat(cuenta.facturaId()).isEqualTo(factura.id());
        assertThat(cuenta.numeroFactura()).isEqualTo(factura.numero());
        assertThat(cuenta.montoOriginal()).isEqualByComparingTo(factura.total());
    }

    @Test
    void obtenerPorFactura_facturaIdInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/factura/999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    // ---------------------------------------------------------------
    // POST /deudores/{id}/abonos
    // ---------------------------------------------------------------

    @Test
    void registrarAbono_montoParcial_reduceSaldoYQuedaEnEstadoParcial() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("100000.00"));

        CuentaPorCobrarResponse actualizada = AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarAbonoRequest(new BigDecimal("30000.00"), MedioPago.EFECTIVO))
                .when().post("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(200)
                .extract().as(CuentaPorCobrarResponse.class);

        assertThat(actualizada.saldoPendiente()).isEqualByComparingTo("70000.00");
        assertThat(actualizada.estado()).isEqualTo(EstadoCuentaPorCobrar.PARCIAL);
        assertThat(actualizada.fechaUltimoAbono()).isNotNull();
    }

    @Test
    void registrarAbono_montoIgualASaldo_quedaEnEstadoPagada() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("50000.00"));

        CuentaPorCobrarResponse actualizada = AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarAbonoRequest(new BigDecimal("50000.00"), MedioPago.EFECTIVO))
                .when().post("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(200)
                .extract().as(CuentaPorCobrarResponse.class);

        assertThat(actualizada.saldoPendiente()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(actualizada.estado()).isEqualTo(EstadoCuentaPorCobrar.PAGADA);
    }

    @Test
    void registrarAbono_montoExcedeSaldo_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("50000.00"));

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarAbonoRequest(new BigDecimal("50000.01"), MedioPago.EFECTIVO))
                .when().post("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(422);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
    }

    /**
     * Known Issue (no es un bug, es una observacion de cobertura): el dominio
     * define MontoAbonoInvalidoException para monto <= 0, pero
     * RegistrarAbonoRequest ya exige @DecimalMin(value = "0", inclusive =
     * false), asi que la validacion @Valid de Bean Validation intercepta
     * cualquier monto <= 0 ANTES de llegar al dominio y responde 400 (no
     * 422). La excepcion de dominio es, en la practica, inalcanzable a
     * traves de la API HTTP con el DTO actual. Se documenta aqui en vez de
     * en el reporte de hallazgos porque no es un comportamiento incorrecto:
     * solo una regla de negocio duplicada/redundante entre capas.
     */
    @Test
    void registrarAbono_montoCeroONegativo_devuelve400PorValidacionDelDto() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("50000.00"));

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarAbonoRequest(BigDecimal.ZERO, MedioPago.EFECTIVO))
                .when().post("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "monto");
    }

    @Test
    void registrarAbono_cuentaYaPagada_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("40000.00"));
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarAbonoRequest(new BigDecimal("40000.00"), MedioPago.EFECTIVO))
                .when().post("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(200);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarAbonoRequest(new BigDecimal("1.00"), MedioPago.EFECTIVO))
                .when().post("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(422);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
    }

    @Test
    void registrarAbono_cuentaAnulada_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        FacturaResponse factura = DeudorTestHelper.crearFacturaCreditoParaClienteNuevo(admin.accessToken(), new BigDecimal("60000.00"));
        CuentaPorCobrarResponse cuenta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/factura/{facturaId}", factura.id())
                .then().statusCode(200)
                .extract().as(CuentaPorCobrarResponse.class);

        // Anular la factura a credito anula automaticamente su cuenta por cobrar (ver AnularFacturaService).
        AuthTestHelper.autenticado(admin.accessToken())
                .when().patch("/facturas/{id}/anular", factura.id())
                .then().statusCode(200);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarAbonoRequest(new BigDecimal("1.00"), MedioPago.EFECTIVO))
                .when().post("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(422);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
    }

    @Test
    void registrarAbono_cuentaInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarAbonoRequest(new BigDecimal("10.00"), MedioPago.EFECTIVO))
                .when().post("/deudores/999999999/abonos")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void registrarAbono_sinAutenticacion_devuelve401() {
        given()
                .contentType(ContentType.JSON)
                .body(new RegistrarAbonoRequest(new BigDecimal("10.00"), MedioPago.EFECTIVO))
                .when().post("/deudores/1/abonos")
                .then().statusCode(401);
    }

    @Test
    void registrarAbono_comoVendedorConPermisoDeudorAbonar_devuelve200() {
        // Documenta que el rol VENDEDOR sembrado tiene DEUDOR_ABONAR (ver clase).
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("20000.00"));

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new RegistrarAbonoRequest(new BigDecimal("5000.00"), MedioPago.EFECTIVO))
                .when().post("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(200);
    }

    // ---------------------------------------------------------------
    // GET /deudores/{id}/abonos
    // ---------------------------------------------------------------

    @Test
    void listarAbonos_conAbonoRegistrado_devuelvePaginaConElAbono() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("30000.00"));
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarAbonoRequest(new BigDecimal("12000.00"), MedioPago.EFECTIVO))
                .when().post("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(200);

        PageResponse<AbonoResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido()).hasSize(1);
        assertThat(pagina.contenido().get(0).monto()).isEqualByComparingTo("12000.00");
        assertThat(pagina.contenido().get(0).cuentaPorCobrarId()).isEqualTo(cuenta.id());
        assertThat(pagina.contenido().get(0).usuarioId()).isNotNull();
    }

    @Test
    void listarAbonos_cuentaInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/999999999/abonos")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    // ---------------------------------------------------------------
    // GET /deudores/{id}/historial
    // ---------------------------------------------------------------

    @Test
    void listarHistorial_incluyeEventoCreacionAlGenerarLaFactura() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("15000.00"));

        PageResponse<HistorialCuentaPorCobrarResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/{id}/historial", cuenta.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(HistorialCuentaPorCobrarResponse::tipoEvento)
                .contains(TipoEventoCuentaPorCobrar.CREACION);
    }

    @Test
    void listarHistorial_incluyeEventoAbonoTrasRegistrarAbonoParcial() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("15000.00"));
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarAbonoRequest(new BigDecimal("5000.00"), MedioPago.EFECTIVO))
                .when().post("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(200);

        PageResponse<HistorialCuentaPorCobrarResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/{id}/historial?size=50", cuenta.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(HistorialCuentaPorCobrarResponse::tipoEvento)
                .contains(TipoEventoCuentaPorCobrar.ABONO);
    }

    @Test
    void listarHistorial_incluyeEventoPagoTotalTrasSaldarLaCuenta() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CuentaPorCobrarResponse cuenta = DeudorTestHelper.crearCuentaPorCobrar(admin.accessToken(), new BigDecimal("15000.00"));
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarAbonoRequest(new BigDecimal("15000.00"), MedioPago.EFECTIVO))
                .when().post("/deudores/{id}/abonos", cuenta.id())
                .then().statusCode(200);

        PageResponse<HistorialCuentaPorCobrarResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/{id}/historial?size=50", cuenta.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(HistorialCuentaPorCobrarResponse::tipoEvento)
                .contains(TipoEventoCuentaPorCobrar.PAGO_TOTAL);
    }

    @Test
    void listarHistorial_cuentaInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/999999999/historial")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    // ---------------------------------------------------------------
    // GET /deudores/clientes/{clienteId}/saldo
    // ---------------------------------------------------------------

    @Test
    void obtenerSaldoPendiente_sumaSaldoDeCuentasActivas() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        DeudorTestHelper.crearCuentaPorCobrarParaCliente(admin.accessToken(), cliente.id(), new BigDecimal("30000.00"));
        DeudorTestHelper.crearCuentaPorCobrarParaCliente(admin.accessToken(), cliente.id(), new BigDecimal("20000.00"));

        SaldoClienteResponse saldo = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/clientes/{clienteId}/saldo", cliente.id())
                .then().statusCode(200)
                .extract().as(SaldoClienteResponse.class);

        assertThat(saldo.clienteId()).isEqualTo(cliente.id());
        assertThat(saldo.saldoPendiente()).isEqualByComparingTo("50000.00");
    }

    @Test
    void obtenerSaldoPendiente_clienteSinCuentas_devuelveSaldoCero() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        SaldoClienteResponse saldo = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/clientes/{clienteId}/saldo", cliente.id())
                .then().statusCode(200)
                .extract().as(SaldoClienteResponse.class);

        assertThat(saldo.saldoPendiente()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void obtenerSaldoPendiente_clienteInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/clientes/999999999/saldo")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }
}
