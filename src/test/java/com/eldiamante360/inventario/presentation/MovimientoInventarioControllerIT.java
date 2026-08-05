package com.eldiamante360.inventario.presentation;

import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.inventario.domain.model.TipoMovimiento;
import com.eldiamante360.inventario.presentation.dto.request.RegistrarMovimientoRequest;
import com.eldiamante360.inventario.presentation.dto.response.MovimientoResponse;
import com.eldiamante360.producto.presentation.ProductoTestHelper;
import com.eldiamante360.producto.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.producto.presentation.dto.response.ProductoResponse;
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
 * Pruebas de integracion de {@link MovimientoInventarioController} (kardex
 * de productos: entradas, salidas y ajustes de stock).
 *
 * <p><b>Nota sobre permisos:</b> el rol VENDEDOR sembrado no tiene
 * INVENTARIO_LEER ni INVENTARIO_AJUSTAR (ver V1__create_seguridad.sql /
 * V2__create_producto_inventario.sql: ambos permisos solo se asignan a
 * ADMIN), por lo que recibe 403 en los dos endpoints de este controlador,
 * pese a tener PRODUCTO_LEER.
 */
class MovimientoInventarioControllerIT extends BaseIntegrationTest {

    // ---------------------------------------------------------------
    // POST /productos/{productoId}/movimientos
    // ---------------------------------------------------------------

    @Test
    void registrar_entradaValida_devuelve201YSumaStock() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProducto(admin.accessToken(),
                ProductoTestHelper.crearCategoriaActiva(admin.accessToken()).id(), BigDecimal.TEN, new BigDecimal("50"));

        var request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA, new BigDecimal("20"), "Compra de mercancia");

        MovimientoResponse movimiento = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(MovimientoResponse.class);

        assertThat(movimiento.id()).isNotNull();
        assertThat(movimiento.productoId()).isEqualTo(producto.id());
        assertThat(movimiento.tipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
        assertThat(movimiento.cantidad()).isEqualByComparingTo("20");
        assertThat(movimiento.stockResultante()).isEqualByComparingTo("70");
        assertThat(movimiento.motivo()).isEqualTo(request.motivo());
        assertThat(movimiento.usuarioId()).isNotNull();
        assertThat(movimiento.fecha()).isNotNull();
    }

    @Test
    void registrar_salidaValida_devuelve201YRestaStock() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProducto(admin.accessToken(),
                ProductoTestHelper.crearCategoriaActiva(admin.accessToken()).id(), BigDecimal.TEN, new BigDecimal("50"));

        var request = new RegistrarMovimientoRequest(TipoMovimiento.SALIDA, new BigDecimal("15"), "Merma de produccion");

        MovimientoResponse movimiento = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(201)
                .extract().as(MovimientoResponse.class);

        assertThat(movimiento.tipoMovimiento()).isEqualTo(TipoMovimiento.SALIDA);
        assertThat(movimiento.stockResultante()).isEqualByComparingTo("35");
    }

    /**
     * Nota: a diferencia de ENTRADA/SALIDA (que suman/restan), AJUSTE
     * establece el stock al valor absoluto indicado en {@code cantidad} (ver
     * {@code Producto.ajustarStock}/{@code RegistrarMovimientoInventarioService}),
     * sin importar el stock previo. Por eso aqui stockResultante == cantidad
     * enviada, no stockPrevio +/- cantidad.
     */
    @Test
    void registrar_ajusteValido_devuelve201YEstableceStockAbsoluto() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProducto(admin.accessToken(),
                ProductoTestHelper.crearCategoriaActiva(admin.accessToken()).id(), BigDecimal.TEN, new BigDecimal("50"));

        var request = new RegistrarMovimientoRequest(TipoMovimiento.AJUSTE, new BigDecimal("999"), "Ajuste por conteo fisico");

        MovimientoResponse movimiento = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(201)
                .extract().as(MovimientoResponse.class);

        assertThat(movimiento.tipoMovimiento()).isEqualTo(TipoMovimiento.AJUSTE);
        assertThat(movimiento.cantidad()).isEqualByComparingTo("999");
        assertThat(movimiento.stockResultante()).isEqualByComparingTo("999");
    }

    @Test
    void registrar_salidaMayorQueStock_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProducto(admin.accessToken(),
                ProductoTestHelper.crearCategoriaActiva(admin.accessToken()).id(), BigDecimal.TEN, new BigDecimal("10"));

        var request = new RegistrarMovimientoRequest(TipoMovimiento.SALIDA, new BigDecimal("11"), "Salida mayor que el stock");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(422);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
    }

    @Test
    void registrar_productoInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA, BigDecimal.TEN, "Entrada IT");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos/999999999/movimientos")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    /**
     * Nota (no es un bug): a diferencia del modulo Insumos Quimicos (donde
     * InsumoInactivoException bloquea movimientos sobre un insumo inactivo),
     * RegistrarMovimientoInventarioService no valida producto.isActivo()
     * antes de registrar el movimiento. Un producto desactivado sigue
     * aceptando entradas/salidas/ajustes de kardex.
     */
    @Test
    void registrar_sobreProductoInactivo_devuelve201() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProducto(admin.accessToken(),
                ProductoTestHelper.crearCategoriaActiva(admin.accessToken()).id(), BigDecimal.TEN, new BigDecimal("50"));
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/productos/{id}/estado", producto.id())
                .then().statusCode(200);

        var request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA, BigDecimal.TEN, "Entrada sobre producto inactivo");

        AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(201);
    }

    @Test
    void registrar_tipoMovimientoNulo_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        String json = "{\"tipoMovimiento\":null,\"cantidad\":10,\"motivo\":\"Motivo valido\"}";

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(json)
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "tipoMovimiento");
    }

    @Test
    void registrar_cantidadNula_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        String json = "{\"tipoMovimiento\":\"ENTRADA\",\"cantidad\":null,\"motivo\":\"Motivo valido\"}";

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(json)
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "cantidad");
    }

    @Test
    void registrar_cantidadNegativa_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        var request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA, new BigDecimal("-1"), "Cantidad invalida");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "cantidad");
    }

    @Test
    void registrar_motivoVacio_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        var request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA, BigDecimal.TEN, "");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "motivo");
    }

    @Test
    void registrar_motivoExcedeLongitudMaxima_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        var request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA, BigDecimal.TEN, "M".repeat(201));

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "motivo");
    }

    @Test
    void registrar_sinAutenticacion_devuelve401() {
        var request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA, BigDecimal.TEN, "Entrada sin auth");

        given().contentType(ContentType.JSON).body(request)
                .when().post("/productos/1/movimientos")
                .then().statusCode(401);
    }

    @Test
    void registrar_comoVendedorSinPermisoInventarioAjustar_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        var request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA, BigDecimal.TEN, "Entrada vendedor");
        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(request)
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // GET /productos/{productoId}/movimientos
    // ---------------------------------------------------------------

    @Test
    void listar_devuelvePaginaConLosMovimientosDelProducto() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        MovimientoResponse registrado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA, new BigDecimal("5"), "Entrada para listado"))
                .when().post("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(201)
                .extract().as(MovimientoResponse.class);

        PageResponse<MovimientoResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(MovimientoResponse::id)
                .contains(registrado.id());
        assertThat(pagina.contenido())
                .allSatisfy(m -> assertThat(m.productoId()).isEqualTo(producto.id()));
    }

    /**
     * Nota (no es un bug): igual que en Insumos Quimicos con
     * ListarLotesPorInsumoService, ListarMovimientosPorProductoService no
     * valida que el producto exista, solo filtra por productoId. Un id
     * inexistente devuelve una pagina vacia con 200 en vez de 404.
     */
    @Test
    void listar_productoInexistente_devuelvePaginaVacia() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        PageResponse<MovimientoResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/productos/999999999/movimientos")
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido()).isEmpty();
    }

    @Test
    void listar_sinAutenticacion_devuelve401() {
        given().when().get("/productos/1/movimientos")
                .then().statusCode(401);
    }

    @Test
    void listar_comoVendedorSinPermisoInventarioLeer_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse producto = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/productos/{productoId}/movimientos", producto.id())
                .then().statusCode(403);
    }
}
