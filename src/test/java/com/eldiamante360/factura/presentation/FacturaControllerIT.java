package com.eldiamante360.factura.presentation;

import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.cliente.presentation.ClienteTestHelper;
import com.eldiamante360.cliente.presentation.dto.response.ClienteResponse;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import com.eldiamante360.deudor.presentation.dto.response.CuentaPorCobrarResponse;
import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.TipoEventoFactura;
import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.factura.presentation.dto.request.CrearFacturaRequest;
import com.eldiamante360.factura.presentation.dto.request.DetalleFacturaRequest;
import com.eldiamante360.factura.presentation.dto.response.FacturaResponse;
import com.eldiamante360.factura.presentation.dto.response.HistorialFacturaResponse;
import com.eldiamante360.producto.presentation.ProductoTestHelper;
import com.eldiamante360.producto.presentation.dto.response.CategoriaResponse;
import com.eldiamante360.producto.presentation.dto.response.ProductoResponse;
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
 * Pruebas de integracion de {@link FacturaController}.
 *
 * <p><b>Nota sobre permisos:</b> este modulo define FACTURA_LEER,
 * FACTURA_CREAR y FACTURA_ANULAR (ver V1__create_seguridad.sql). El rol
 * VENDEDOR sembrado tiene FACTURA_LEER y FACTURA_CREAR, pero NO
 * FACTURA_ANULAR. Por lo tanto, con los roles sembrados (ADMIN/VENDEDOR) el
 * unico escenario 403 ejercitable es sobre {@code PATCH /facturas/{id}/anular};
 * los endpoints protegidos con FACTURA_LEER/FACTURA_CREAR no tienen ningun
 * escenario de permiso insuficiente disponible (ambos roles los tienen), asi
 * que se documentan con pruebas positivas explicitas para VENDEDOR en vez de
 * omitirse (mismo patron que {@code DeudorControllerIT}).
 *
 * <p><b>Nota sobre el flujo de negocio:</b> emitir una factura descuenta
 * automaticamente el stock del producto (via {@code Producto.registrarSalida})
 * y deja un movimiento SALIDA en el kardex; anularla revierte el stock (via
 * {@code Producto.registrarEntrada}) con un movimiento ENTRADA. Si el tipo de
 * pago es CREDITO, ademas se genera/anula automaticamente una
 * CuentaPorCobrar en el modulo Deudores, en la misma transaccion (ver
 * {@link com.eldiamante360.factura.application.usecase.CrearFacturaService}
 * y {@link com.eldiamante360.factura.application.usecase.AnularFacturaService}).
 */
class FacturaControllerIT extends BaseIntegrationTest {

    // ---------------------------------------------------------------
    // POST /facturas
    // ---------------------------------------------------------------

    @Test
    void crear_contadoDatosValidos_devuelve201YDescuentaStock() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());
        ProductoResponse producto = ProductoTestHelper.crearProducto(admin.accessToken(), categoria.id(),
                new BigDecimal("10000.00"), new BigDecimal("50"));
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var detalle = new DetalleFacturaRequest(producto.id(), new BigDecimal("3"), BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        FacturaResponse creada = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(FacturaResponse.class);

        assertThat(creada.id()).isNotNull();
        assertThat(creada.numero()).isNotBlank();
        assertThat(creada.clienteId()).isEqualTo(cliente.id());
        assertThat(creada.clienteNombre()).isEqualTo(cliente.nombre());
        assertThat(creada.clienteNumeroDocumento()).isEqualTo(cliente.numeroDocumento());
        assertThat(creada.tipoPago()).isEqualTo(TipoPago.CONTADO);
        assertThat(creada.estado()).isEqualTo(EstadoFactura.EMITIDA);
        assertThat(creada.detalles()).hasSize(1);
        assertThat(creada.detalles().get(0).productoId()).isEqualTo(producto.id());
        assertThat(creada.detalles().get(0).productoNombre()).isEqualTo(producto.nombre());
        assertThat(creada.detalles().get(0).cantidad()).isEqualByComparingTo("3");
        assertThat(creada.detalles().get(0).precioUnitario()).isEqualByComparingTo("10000.00");
        assertThat(creada.detalles().get(0).subtotal()).isEqualByComparingTo("30000.00");
        assertThat(creada.detalles().get(0).descuento()).isEqualByComparingTo("0.00");
        assertThat(creada.detalles().get(0).total()).isEqualByComparingTo("30000.00");
        assertThat(creada.subtotal()).isEqualByComparingTo("30000.00");
        assertThat(creada.descuento()).isEqualByComparingTo("0.00");
        assertThat(creada.total()).isEqualByComparingTo("30000.00");
        assertThat(creada.usuarioId()).isNotNull();
        assertThat(creada.fecha()).isNotNull();
        assertThat(creada.fechaAnulacion()).isNull();

        ProductoResponse productoActualizado = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/productos/{id}", producto.id())
                .then().statusCode(200)
                .extract().as(ProductoResponse.class);
        assertThat(productoActualizado.stockActual()).isEqualByComparingTo("47");
    }

    @Test
    void crear_conPorcentajeDescuento_calculaSubtotalDescuentoYTotalConEscala2() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), new BigDecimal("10000.00"));
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var detalle = new DetalleFacturaRequest(producto.id(), new BigDecimal("3"), new BigDecimal("10"));
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        FacturaResponse creada = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(201)
                .extract().as(FacturaResponse.class);

        // subtotal = 3 * 10000.00 = 30000.00; descuento = 30000.00 * 10% = 3000.00; total = 27000.00
        assertThat(creada.detalles().get(0).subtotal()).isEqualByComparingTo("30000.00");
        assertThat(creada.detalles().get(0).descuento()).isEqualByComparingTo("3000.00");
        assertThat(creada.detalles().get(0).total()).isEqualByComparingTo("27000.00");
        assertThat(creada.subtotal()).isEqualByComparingTo("30000.00");
        assertThat(creada.descuento()).isEqualByComparingTo("3000.00");
        assertThat(creada.total()).isEqualByComparingTo("27000.00");
    }

    @Test
    void crear_variosDetalles_sumaSubtotalDescuentoYTotalDeTodasLasLineas() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse productoA = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), new BigDecimal("10000.00"));
        ProductoResponse productoB = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), new BigDecimal("5000.00"));
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var detalleA = new DetalleFacturaRequest(productoA.id(), new BigDecimal("2"), BigDecimal.ZERO); // 20000.00
        var detalleB = new DetalleFacturaRequest(productoB.id(), new BigDecimal("4"), BigDecimal.ZERO); // 20000.00
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalleA, detalleB), null);

        FacturaResponse creada = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(201)
                .extract().as(FacturaResponse.class);

        assertThat(creada.detalles()).hasSize(2);
        assertThat(creada.subtotal()).isEqualByComparingTo("40000.00");
        assertThat(creada.total()).isEqualByComparingTo("40000.00");
    }

    @Test
    void crear_creditoDatosValidos_devuelve201YGeneraCuentaPorCobrarPendiente() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), new BigDecimal("25000.00"));
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var detalle = new DetalleFacturaRequest(producto.id(), BigDecimal.ONE, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CREDITO, List.of(detalle), null);

        FacturaResponse creada = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(201)
                .extract().as(FacturaResponse.class);

        assertThat(creada.tipoPago()).isEqualTo(TipoPago.CREDITO);

        CuentaPorCobrarResponse cuenta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/factura/{facturaId}", creada.id())
                .then().statusCode(200)
                .extract().as(CuentaPorCobrarResponse.class);

        assertThat(cuenta.facturaId()).isEqualTo(creada.id());
        assertThat(cuenta.clienteId()).isEqualTo(cliente.id());
        assertThat(cuenta.montoOriginal()).isEqualByComparingTo("25000.00");
        assertThat(cuenta.saldoPendiente()).isEqualByComparingTo("25000.00");
        assertThat(cuenta.estado()).isEqualTo(EstadoCuentaPorCobrar.PENDIENTE);
    }

    @Test
    void crear_clienteInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);

        var detalle = new DetalleFacturaRequest(producto.id(), BigDecimal.ONE, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(999_999_999L, TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void crear_clienteInactivo_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        AuthTestHelper.autenticado(admin.accessToken())
                .body(new com.eldiamante360.cliente.presentation.dto.request.CambiarEstadoRequest(false))
                .when().patch("/clientes/{id}/estado", cliente.id())
                .then().statusCode(200);

        var detalle = new DetalleFacturaRequest(producto.id(), BigDecimal.ONE, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).contains(cliente.id().toString());
    }

    @Test
    void crear_productoInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var detalle = new DetalleFacturaRequest(999_999_999L, BigDecimal.ONE, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void crear_productoInactivo_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        AuthTestHelper.autenticado(admin.accessToken())
                .body(new com.eldiamante360.producto.presentation.dto.request.CambiarEstadoRequest(false))
                .when().patch("/productos/{id}/estado", producto.id())
                .then().statusCode(200);

        var detalle = new DetalleFacturaRequest(producto.id(), BigDecimal.ONE, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).contains(producto.nombre());
    }

    @Test
    void crear_stockInsuficiente_devuelve422YNoAlteraElStock() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProducto(admin.accessToken(),
                ProductoTestHelper.crearCategoriaActiva(admin.accessToken()).id(), BigDecimal.TEN, new BigDecimal("5"));
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var detalle = new DetalleFacturaRequest(producto.id(), new BigDecimal("10"), BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(422);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);

        ProductoResponse productoTrasFallo = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/productos/{id}", producto.id())
                .then().statusCode(200)
                .extract().as(ProductoResponse.class);
        assertThat(productoTrasFallo.stockActual()).isEqualByComparingTo("5");
    }

    @Test
    void crear_sinDetalles_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "detalles");
    }

    @Test
    void crear_clienteIdNulo_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        var detalle = new DetalleFacturaRequest(producto.id(), BigDecimal.ONE, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(null, TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "clienteId");
    }

    @Test
    void crear_tipoPagoNulo_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);

        String json = """
                {"clienteId": %d, "tipoPago": null, "detalles": [{"productoId": %d, "cantidad": 1, "porcentajeDescuento": 0}]}
                """.formatted(cliente.id(), producto.id());

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(json)
                .when().post("/facturas")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "tipoPago");
    }

    @Test
    void crear_detalleProductoIdNulo_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        var detalle = new DetalleFacturaRequest(null, BigDecimal.ONE, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "detalles[0].productoId");
    }

    @Test
    void crear_detalleCantidadNula_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        var detalle = new DetalleFacturaRequest(producto.id(), null, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "detalles[0].cantidad");
    }

    @Test
    void crear_detalleCantidadCero_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        var detalle = new DetalleFacturaRequest(producto.id(), BigDecimal.ZERO, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "detalles[0].cantidad");
    }

    @Test
    void crear_detalleCantidadNegativa_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        var detalle = new DetalleFacturaRequest(producto.id(), new BigDecimal("-1"), BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "detalles[0].cantidad");
    }

    @Test
    void crear_detallePorcentajeDescuentoNulo_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        var detalle = new DetalleFacturaRequest(producto.id(), BigDecimal.ONE, null);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "detalles[0].porcentajeDescuento");
    }

    @Test
    void crear_detallePorcentajeDescuentoNegativo_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        var detalle = new DetalleFacturaRequest(producto.id(), BigDecimal.ONE, new BigDecimal("-5"));
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "detalles[0].porcentajeDescuento");
    }

    @Test
    void crear_detallePorcentajeDescuentoMayorA100_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        var detalle = new DetalleFacturaRequest(producto.id(), BigDecimal.ONE, new BigDecimal("101"));
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "detalles[0].porcentajeDescuento");
    }

    @Test
    void crear_sinAutenticacion_devuelve401() {
        var detalle = new DetalleFacturaRequest(1L, BigDecimal.ONE, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(1L, TipoPago.CONTADO, List.of(detalle), null);

        given().contentType(ContentType.JSON).body(request)
                .when().post("/facturas")
                .then().statusCode(401);
    }

    @Test
    void crear_comoVendedorConPermisoFacturaCrear_devuelve201() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        var detalle = new DetalleFacturaRequest(producto.id(), BigDecimal.ONE, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CONTADO, List.of(detalle), null);

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(201);
    }

    // ---------------------------------------------------------------
    // GET /facturas
    // ---------------------------------------------------------------

    @Test
    void listar_devuelvePaginaConFormaEstandarYLaFacturaCreada() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        FacturaResponse creada = crearFacturaContado(admin.accessToken(), cliente.id(), producto.id());

        PageResponse<FacturaResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/facturas?size=200")
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido()).extracting(FacturaResponse::id).contains(creada.id());
        assertThat(pagina.totalElementos()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void listar_filtradoPorClienteId_devuelveSoloLasFacturasDeEseCliente() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse clienteA = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        ClienteResponse clienteB = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        FacturaResponse facturaA = crearFacturaContado(admin.accessToken(), clienteA.id(), producto.id());
        crearFacturaContado(admin.accessToken(), clienteB.id(), producto.id());

        PageResponse<FacturaResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/facturas?clienteId={clienteId}", clienteA.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido()).extracting(FacturaResponse::id).contains(facturaA.id());
        assertThat(pagina.contenido()).allSatisfy(f -> assertThat(f.clienteId()).isEqualTo(clienteA.id()));
    }

    @Test
    void listar_filtradoPorClienteIdInexistente_devuelve404() {
        // Nota (no es un bug): a diferencia de otros listados por padre en la
        // app (p.ej. lotes/movimientos de Insumos Quimicos), este SI valida
        // que el cliente exista antes de listar (ver
        // ListarFacturasPorClienteService), comportamiento explicito y
        // deliberado, distinto al resto de la app.
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/facturas?clienteId=999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void listar_filtradoPorEstado_devuelveSoloFacturasConEseEstado() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        FacturaResponse anulada = crearFacturaContado(admin.accessToken(), cliente.id(), producto.id());
        AuthTestHelper.autenticado(admin.accessToken())
                .when().patch("/facturas/{id}/anular", anulada.id())
                .then().statusCode(200);
        FacturaResponse emitida = crearFacturaContado(admin.accessToken(), cliente.id(), producto.id());

        PageResponse<FacturaResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/facturas?estado=ANULADA&size=200")
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido()).extracting(FacturaResponse::id).contains(anulada.id());
        assertThat(pagina.contenido()).extracting(FacturaResponse::id).doesNotContain(emitida.id());
        assertThat(pagina.contenido()).allSatisfy(f -> assertThat(f.estado()).isEqualTo(EstadoFactura.ANULADA));
    }

    @Test
    void listar_sinAutenticacion_devuelve401() {
        given().when().get("/facturas")
                .then().statusCode(401);
    }

    @Test
    void listar_comoVendedorConPermisoFacturaLeer_devuelve200() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/facturas")
                .then().statusCode(200);
    }

    // ---------------------------------------------------------------
    // GET /facturas/{id}
    // ---------------------------------------------------------------

    @Test
    void obtener_idExistente_devuelveLaFacturaCreada() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        FacturaResponse creada = crearFacturaContado(admin.accessToken(), cliente.id(), producto.id());

        FacturaResponse obtenida = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/facturas/{id}", creada.id())
                .then().statusCode(200)
                .extract().as(FacturaResponse.class);

        assertThat(obtenida.id()).isEqualTo(creada.id());
        assertThat(obtenida.numero()).isEqualTo(creada.numero());
    }

    @Test
    void obtener_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/facturas/999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void obtener_sinAutenticacion_devuelve401() {
        given().when().get("/facturas/1")
                .then().statusCode(401);
    }

    // ---------------------------------------------------------------
    // GET /facturas/{id}/pdf
    // ---------------------------------------------------------------

    @Test
    void obtenerPdf_facturaExistente_devuelvePdfValido() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        FacturaResponse creada = crearFacturaContado(admin.accessToken(), cliente.id(), producto.id());

        byte[] pdf = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/facturas/{id}/pdf", creada.id())
                .then().statusCode(200)
                .contentType("application/pdf")
                .extract().asByteArray();

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 5, java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
    }

    @Test
    void obtenerPdf_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/facturas/999999999/pdf")
                .then().statusCode(404);
    }

    @Test
    void obtenerPdf_sinAutenticacion_devuelve401() {
        given().when().get("/facturas/1/pdf")
                .then().statusCode(401);
    }

    // ---------------------------------------------------------------
    // PATCH /facturas/{id}/anular
    // ---------------------------------------------------------------

    @Test
    void anular_facturaContado_devuelve200YRestauraStock() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());
        ProductoResponse producto = ProductoTestHelper.crearProducto(admin.accessToken(), categoria.id(),
                new BigDecimal("10000.00"), new BigDecimal("20"));
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        FacturaResponse creada = crearFacturaContado(admin.accessToken(), cliente.id(), producto.id());

        FacturaResponse anulada = AuthTestHelper.autenticado(admin.accessToken())
                .when().patch("/facturas/{id}/anular", creada.id())
                .then().statusCode(200)
                .extract().as(FacturaResponse.class);

        assertThat(anulada.estado()).isEqualTo(EstadoFactura.ANULADA);
        assertThat(anulada.fechaAnulacion()).isNotNull();

        ProductoResponse productoTrasAnular = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/productos/{id}", producto.id())
                .then().statusCode(200)
                .extract().as(ProductoResponse.class);
        assertThat(productoTrasAnular.stockActual()).isEqualByComparingTo("20");
    }

    @Test
    void anular_facturaCredito_devuelve200YAnulaLaCuentaPorCobrar() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), new BigDecimal("18000.00"));
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());

        var detalle = new DetalleFacturaRequest(producto.id(), BigDecimal.ONE, BigDecimal.ZERO);
        var request = new CrearFacturaRequest(cliente.id(), TipoPago.CREDITO, List.of(detalle), null);
        FacturaResponse creada = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/facturas")
                .then().statusCode(201)
                .extract().as(FacturaResponse.class);

        AuthTestHelper.autenticado(admin.accessToken())
                .when().patch("/facturas/{id}/anular", creada.id())
                .then().statusCode(200);

        CuentaPorCobrarResponse cuenta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/deudores/factura/{facturaId}", creada.id())
                .then().statusCode(200)
                .extract().as(CuentaPorCobrarResponse.class);

        assertThat(cuenta.estado()).isEqualTo(EstadoCuentaPorCobrar.ANULADA);
        assertThat(cuenta.fechaAnulacion()).isNotNull();
    }

    @Test
    void anular_facturaYaAnulada_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        FacturaResponse creada = crearFacturaContado(admin.accessToken(), cliente.id(), producto.id());

        AuthTestHelper.autenticado(admin.accessToken())
                .when().patch("/facturas/{id}/anular", creada.id())
                .then().statusCode(200);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().patch("/facturas/{id}/anular", creada.id())
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).contains(creada.id().toString());
    }

    @Test
    void anular_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().patch("/facturas/999999999/anular")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void anular_sinAutenticacion_devuelve401() {
        given().when().patch("/facturas/1/anular")
                .then().statusCode(401);
    }

    @Test
    void anular_comoVendedorSinPermisoFacturaAnular_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        FacturaResponse creada = crearFacturaContado(admin.accessToken(), cliente.id(), producto.id());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().patch("/facturas/{id}/anular", creada.id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // GET /facturas/{id}/historial
    // ---------------------------------------------------------------

    @Test
    void historial_devuelvePaginaConElEventoDeCreacion() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        FacturaResponse creada = crearFacturaContado(admin.accessToken(), cliente.id(), producto.id());

        PageResponse<HistorialFacturaResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/facturas/{id}/historial", creada.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .anySatisfy(evento -> {
                    assertThat(evento.facturaId()).isEqualTo(creada.id());
                    assertThat(evento.tipoEvento()).isEqualTo(TipoEventoFactura.CREACION);
                    assertThat(evento.fecha()).isNotNull();
                });
    }

    @Test
    void historial_despuesDeAnular_incluyeElEventoDeAnulacion() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ClienteResponse cliente = ClienteTestHelper.crearClienteMinimo(admin.accessToken());
        FacturaResponse creada = crearFacturaContado(admin.accessToken(), cliente.id(), producto.id());

        AuthTestHelper.autenticado(admin.accessToken())
                .when().patch("/facturas/{id}/anular", creada.id())
                .then().statusCode(200);

        PageResponse<HistorialFacturaResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/facturas/{id}/historial", creada.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(HistorialFacturaResponse::tipoEvento)
                .contains(TipoEventoFactura.CREACION, TipoEventoFactura.ANULACION);
    }

    @Test
    void historial_facturaInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/facturas/999999999/historial")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void historial_sinAutenticacion_devuelve401() {
        given().when().get("/facturas/1/historial")
                .then().statusCode(401);
    }

    // ---------------------------------------------------------------
    // Helpers locales
    // ---------------------------------------------------------------

    /** Delega en {@link FacturaTestHelper#crearFacturaContado}: 1 unidad, sin descuento, tipoPago CONTADO. */
    private static FacturaResponse crearFacturaContado(String accessTokenAdmin, Long clienteId, Long productoId) {
        return FacturaTestHelper.crearFacturaContado(accessTokenAdmin, clienteId, productoId);
    }
}
