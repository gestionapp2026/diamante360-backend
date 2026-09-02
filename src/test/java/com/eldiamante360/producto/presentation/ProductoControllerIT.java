package com.eldiamante360.producto.presentation;

import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.producto.domain.model.TipoVenta;
import com.eldiamante360.producto.domain.model.UnidadMedida;
import com.eldiamante360.producto.presentation.dto.request.ActualizarProductoRequest;
import com.eldiamante360.producto.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.producto.presentation.dto.request.CrearProductoRequest;
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
 * Pruebas de integracion de {@link ProductoController}.
 *
 * <p><b>Nota sobre permisos:</b> ver Javadoc de {@link CategoriaControllerIT}
 * -- Producto y Categoria comparten el mismo conjunto de 4 permisos
 * (PRODUCTO_CREAR/LEER/EDITAR/ELIMINAR); el rol VENDEDOR sembrado solo tiene
 * PRODUCTO_LEER.
 */
class ProductoControllerIT extends BaseIntegrationTest {

    // ---------------------------------------------------------------
    // POST /productos
    // ---------------------------------------------------------------

    @Test
    void crear_datosValidos_devuelve201YCuerpo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto IT"), categoria.id(),
                TipoVenta.UNIDAD, UnidadMedida.UND, new BigDecimal("10000"), new BigDecimal("15000"),
                new BigDecimal("50"), new BigDecimal("5"));

        ProductoResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(ProductoResponse.class);

        assertThat(creado.id()).isNotNull();
        assertThat(creado.nombre()).isEqualTo(request.nombre());
        assertThat(creado.categoriaId()).isEqualTo(categoria.id());
        assertThat(creado.categoriaNombre()).isEqualTo(categoria.nombre());
        assertThat(creado.tipoVenta()).isEqualTo(TipoVenta.UNIDAD);
        assertThat(creado.unidadMedida()).isEqualTo(UnidadMedida.UND);
        assertThat(creado.precioCompra()).isEqualByComparingTo("10000");
        assertThat(creado.precioVenta()).isEqualByComparingTo("15000");
        assertThat(creado.stockActual()).isEqualByComparingTo("50");
        assertThat(creado.stockMinimo()).isEqualByComparingTo("5");
        assertThat(creado.stockBajo()).isFalse();
        assertThat(creado.activo()).isTrue();
    }

    @Test
    void crear_stockInicialIgualAStockMinimo_devuelveStockBajoTrue() {
        // tieneStockBajo() usa <= (ver Producto.tieneStockBajo()): en el
        // limite exacto ya se considera stock bajo.
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Stock Limite"), categoria.id(),
                TipoVenta.UNIDAD, UnidadMedida.UND, BigDecimal.TEN, BigDecimal.TEN,
                new BigDecimal("10"), new BigDecimal("10"));

        ProductoResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(201)
                .extract().as(ProductoResponse.class);

        assertThat(creado.stockBajo()).isTrue();
    }

    @Test
    void crear_stockInicialMenorQueStockMinimo_devuelveStockBajoTrue() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Stock Bajo"), categoria.id(),
                TipoVenta.UNIDAD, UnidadMedida.UND, BigDecimal.TEN, BigDecimal.TEN,
                new BigDecimal("2"), new BigDecimal("10"));

        ProductoResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(201)
                .extract().as(ProductoResponse.class);

        assertThat(creado.stockBajo()).isTrue();
    }

    @Test
    void crear_pesoVariableConKg_devuelve201() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Chuleta"), categoria.id(),
                TipoVenta.PESO_VARIABLE, UnidadMedida.KG, new BigDecimal("8000"), new BigDecimal("12000"),
                new BigDecimal("30.500"), new BigDecimal("5"));

        ProductoResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(201)
                .extract().as(ProductoResponse.class);

        assertThat(creado.tipoVenta()).isEqualTo(TipoVenta.PESO_VARIABLE);
        assertThat(creado.unidadMedida()).isEqualTo(UnidadMedida.KG);
    }

    @Test
    void crear_pesoVariableConLb_devuelve201() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Lomo"), categoria.id(),
                TipoVenta.PESO_VARIABLE, UnidadMedida.LB, new BigDecimal("8000"), new BigDecimal("12000"),
                new BigDecimal("30"), new BigDecimal("5"));

        ProductoResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(201)
                .extract().as(ProductoResponse.class);

        assertThat(creado.unidadMedida()).isEqualTo(UnidadMedida.LB);
    }

    @Test
    void crear_tipoVentaUnidadConUnidadMedidaKg_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Invalido"), categoria.id(),
                TipoVenta.UNIDAD, UnidadMedida.KG, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ONE, BigDecimal.ZERO);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).contains("UNIDAD").contains("UND");
    }

    @Test
    void crear_tipoVentaPesoVariableConUnidadMedidaUnd_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Invalido 2"), categoria.id(),
                TipoVenta.PESO_VARIABLE, UnidadMedida.UND, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ONE, BigDecimal.ZERO);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).contains("PESO_VARIABLE");
    }

    @Test
    void crear_nombreDuplicado_devuelve409() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse existente = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        CategoriaResponse otraCategoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new CrearProductoRequest(existente.nombre(), otraCategoria.id(),
                TipoVenta.UNIDAD, UnidadMedida.UND, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ONE, BigDecimal.ZERO);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(409);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 409);
        assertThat(error.message()).contains(existente.nombre());
    }

    @Test
    void crear_categoriaInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Categoria Fake"), 999_999_999L,
                TipoVenta.UNIDAD, UnidadMedida.UND, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ONE, BigDecimal.ZERO);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void crear_categoriaInactiva_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/categorias/{id}/estado", categoria.id())
                .then().statusCode(200);

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Categoria Inactiva"), categoria.id(),
                TipoVenta.UNIDAD, UnidadMedida.UND, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ONE, BigDecimal.ZERO);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).contains(categoria.nombre());
    }

    @Test
    void crear_camposObligatoriosFaltantes_devuelve400ConVariosErroresDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var request = new CrearProductoRequest("", null, null, null, null, null, null, null);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombre");
        ApiErrorAssertions.verificarErrorDeCampo(error, "categoriaId");
        ApiErrorAssertions.verificarErrorDeCampo(error, "tipoVenta");
        ApiErrorAssertions.verificarErrorDeCampo(error, "unidadMedida");
        ApiErrorAssertions.verificarErrorDeCampo(error, "precioCompra");
        ApiErrorAssertions.verificarErrorDeCampo(error, "stockInicial");
        ApiErrorAssertions.verificarErrorDeCampo(error, "stockMinimo");
    }

    @Test
    void crear_precioVentaNegativo_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Precio Negativo"), categoria.id(),
                TipoVenta.UNIDAD, UnidadMedida.UND, BigDecimal.TEN, new BigDecimal("-1"),
                BigDecimal.ONE, BigDecimal.ZERO);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "precioVenta");
    }

    @Test
    void crear_sinPrecioVenta_creaConPrecioVentaCero() {
        // El precio de venta ya no es obligatorio al crear: el vendedor puede registrar el producto
        // sobre la marcha y el precio se define despues editandolo (ver CrearProductoService).
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Sin Precio Venta"),
                categoria.id(), TipoVenta.UNIDAD, UnidadMedida.UND, BigDecimal.TEN, null,
                BigDecimal.ONE, BigDecimal.ZERO);

        ProductoResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(201)
                .extract().as(ProductoResponse.class);

        assertThat(creado.precioVenta()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void crear_sinAutenticacion_devuelve401() {
        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Sin Auth"), 1L,
                TipoVenta.UNIDAD, UnidadMedida.UND, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ONE, BigDecimal.ZERO);

        given().contentType(ContentType.JSON).body(request)
                .when().post("/productos")
                .then().statusCode(401);
    }

    @Test
    void crear_comoVendedorConPermisoProductoCrear_devuelve201() {
        // VENDEDOR tiene PRODUCTO_CREAR (V21__permisos_vendedor_crear_cliente_producto.sql): puede
        // registrar un producto nuevo sobre la marcha al facturar, sin depender de un admin.
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        var request = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Vendedor"), categoria.id(),
                TipoVenta.UNIDAD, UnidadMedida.UND, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ONE, BigDecimal.ZERO);

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(request)
                .when().post("/productos")
                .then().statusCode(201);
    }

    // ---------------------------------------------------------------
    // GET /productos
    // ---------------------------------------------------------------

    @Test
    void listar_devuelvePaginaConFormaEstandarYElProductoCreado() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse creado = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);

        PageResponse<ProductoResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/productos?size=200")
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        assertThat(pagina.contenido())
                .extracting(ProductoResponse::id)
                .contains(creado.id());
    }

    @Test
    void listar_sinAutenticacion_devuelve401() {
        given().when().get("/productos")
                .then().statusCode(401);
    }

    @Test
    void listar_comoVendedorConPermisoProductoLeer_devuelve200() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/productos")
                .then().statusCode(200);
    }

    // ---------------------------------------------------------------
    // GET /productos/stock-bajo
    // ---------------------------------------------------------------

    @Test
    void listarStockBajo_incluyeConStockBajoYExcluyeConStockSuficiente() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var requestBajo = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Con Stock Bajo"), categoria.id(),
                TipoVenta.UNIDAD, UnidadMedida.UND, BigDecimal.TEN, BigDecimal.TEN,
                new BigDecimal("1"), new BigDecimal("10"));
        ProductoResponse conStockBajo = AuthTestHelper.autenticado(admin.accessToken())
                .body(requestBajo)
                .when().post("/productos")
                .then().statusCode(201)
                .extract().as(ProductoResponse.class);

        var requestSuficiente = new CrearProductoRequest(TestDataFactory.nombreCompleto("Producto Con Stock Suficiente"), categoria.id(),
                TipoVenta.UNIDAD, UnidadMedida.UND, BigDecimal.TEN, BigDecimal.TEN,
                new BigDecimal("1000"), new BigDecimal("10"));
        ProductoResponse conStockSuficiente = AuthTestHelper.autenticado(admin.accessToken())
                .body(requestSuficiente)
                .when().post("/productos")
                .then().statusCode(201)
                .extract().as(ProductoResponse.class);

        List<ProductoResponse> stockBajo = List.of(AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/productos/stock-bajo")
                .then().statusCode(200)
                .extract().as(ProductoResponse[].class));

        assertThat(stockBajo).extracting(ProductoResponse::id).contains(conStockBajo.id());
        assertThat(stockBajo).extracting(ProductoResponse::id).doesNotContain(conStockSuficiente.id());
        assertThat(stockBajo).allSatisfy(p -> assertThat(p.stockBajo()).isTrue());
    }

    @Test
    void listarStockBajo_sinAutenticacion_devuelve401() {
        given().when().get("/productos/stock-bajo")
                .then().statusCode(401);
    }

    // ---------------------------------------------------------------
    // GET /productos/{id}
    // ---------------------------------------------------------------

    @Test
    void obtener_idExistente_devuelveElProductoCreado() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse creado = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), new BigDecimal("25000"));

        ProductoResponse obtenido = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/productos/{id}", creado.id())
                .then().statusCode(200)
                .extract().as(ProductoResponse.class);

        assertThat(obtenido.id()).isEqualTo(creado.id());
        assertThat(obtenido.nombre()).isEqualTo(creado.nombre());
        assertThat(obtenido.precioVenta()).isEqualByComparingTo(creado.precioVenta());
    }

    @Test
    void obtener_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/productos/999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void obtener_sinAutenticacion_devuelve401() {
        given().when().get("/productos/1")
                .then().statusCode(401);
    }

    // ---------------------------------------------------------------
    // PUT /productos/{id}
    // ---------------------------------------------------------------

    @Test
    void actualizar_datosValidos_devuelve200YMantieneTipoVentaYUnidadMedidaInmutables() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoriaOriginal = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());
        ProductoResponse creado = ProductoTestHelper.crearProducto(admin.accessToken(), categoriaOriginal.id(),
                BigDecimal.TEN, new BigDecimal("100"));
        CategoriaResponse categoriaNueva = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new ActualizarProductoRequest(TestDataFactory.nombreCompleto("Producto Actualizado"), categoriaNueva.id(),
                new BigDecimal("20000"), new BigDecimal("30000"), new BigDecimal("15"));

        ProductoResponse actualizado = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/productos/{id}", creado.id())
                .then().statusCode(200)
                .extract().as(ProductoResponse.class);

        assertThat(actualizado.id()).isEqualTo(creado.id());
        assertThat(actualizado.nombre()).isEqualTo(request.nombre());
        assertThat(actualizado.categoriaId()).isEqualTo(categoriaNueva.id());
        assertThat(actualizado.precioCompra()).isEqualByComparingTo("20000");
        assertThat(actualizado.precioVenta()).isEqualByComparingTo("30000");
        assertThat(actualizado.stockMinimo()).isEqualByComparingTo("15");
        // tipoVenta y unidadMedida son inmutables: ActualizarProductoRequest ni
        // siquiera los expone (ver ActualizarProductoService/Producto.actualizarDatos).
        assertThat(actualizado.tipoVenta()).isEqualTo(creado.tipoVenta());
        assertThat(actualizado.unidadMedida()).isEqualTo(creado.unidadMedida());
        // stockActual tampoco se ve afectado por este endpoint: solo se mueve
        // via movimientos de inventario (kardex), no via actualizacion de datos.
        assertThat(actualizado.stockActual()).isEqualByComparingTo(creado.stockActual());
    }

    @Test
    void actualizar_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoria = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());
        var request = new ActualizarProductoRequest(TestDataFactory.nombreCompleto("Producto"), categoria.id(),
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/productos/999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void actualizar_categoriaInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse creado = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);

        var request = new ActualizarProductoRequest(TestDataFactory.nombreCompleto("Producto"), 999_999_999L,
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/productos/{id}", creado.id())
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void actualizar_categoriaInactiva_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse creado = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        CategoriaResponse categoriaInactiva = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/categorias/{id}/estado", categoriaInactiva.id())
                .then().statusCode(200);

        var request = new ActualizarProductoRequest(TestDataFactory.nombreCompleto("Producto"), categoriaInactiva.id(),
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/productos/{id}", creado.id())
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).contains(categoriaInactiva.nombre());
    }

    @Test
    void actualizar_nombreVacio_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse creado = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);

        var request = new ActualizarProductoRequest("", creado.categoriaId(), BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/productos/{id}", creado.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombre");
    }

    /**
     * Known Issue: al igual que en Categoria (ver
     * {@link CategoriaControllerIT#actualizar_nombreDuplicadoDeOtraCategoria_devuelve500EnVezDe409_KnownIssue}),
     * {@code PUT /productos/{id}} (ver {@code ActualizarProductoService}) NO
     * valida duplicados de nombre contra otros productos antes de guardar,
     * a diferencia de {@code POST /productos} que si lo hace. La tabla si
     * tiene una restriccion UNIQUE ({@code uq_producto_nombre}, ver
     * V2__create_producto_inventario.sql), asi que el guardado falla, pero
     * {@code GlobalExceptionHandler} no traduce
     * {@code DataIntegrityViolationException} a un 409 controlado: cae en el
     * handler generico de {@code Exception} y responde <b>500</b>
     * ("Ocurrio un error inesperado") en vez de 409. Se documenta el
     * comportamiento real (500) sin modificar produccion.
     */
    @Test
    void actualizar_nombreDuplicadoDeOtroProducto_devuelve500EnVezDe409_KnownIssue() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse productoA = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        ProductoResponse productoB = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);

        var request = new ActualizarProductoRequest(productoA.nombre(), productoB.categoriaId(),
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);

        AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/productos/{id}", productoB.id())
                .then().statusCode(500);
    }

    @Test
    void actualizar_sinAutenticacion_devuelve401() {
        var request = new ActualizarProductoRequest(TestDataFactory.nombreCompleto("Producto"), 1L,
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);

        given().contentType(ContentType.JSON).body(request)
                .when().put("/productos/1")
                .then().statusCode(401);
    }

    @Test
    void actualizar_comoVendedorSinPermisoProductoEditar_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse creado = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        var request = new ActualizarProductoRequest(TestDataFactory.nombreCompleto("Producto"), creado.categoriaId(),
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);
        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(request)
                .when().put("/productos/{id}", creado.id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // PATCH /productos/{id}/estado
    // ---------------------------------------------------------------

    @Test
    void cambiarEstado_desactivar_devuelve200ConActivoFalse() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse creado = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);

        ProductoResponse actualizado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/productos/{id}/estado", creado.id())
                .then().statusCode(200)
                .extract().as(ProductoResponse.class);

        assertThat(actualizado.activo()).isFalse();
    }

    @Test
    void cambiarEstado_reactivar_devuelve200ConActivoTrue() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse creado = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/productos/{id}/estado", creado.id())
                .then().statusCode(200);

        ProductoResponse actualizado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(true))
                .when().patch("/productos/{id}/estado", creado.id())
                .then().statusCode(200)
                .extract().as(ProductoResponse.class);

        assertThat(actualizado.activo()).isTrue();
    }

    @Test
    void cambiarEstado_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/productos/999999999/estado")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void cambiarEstado_activoNulo_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse creado = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(null))
                .when().patch("/productos/{id}/estado", creado.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "activo");
    }

    @Test
    void cambiarEstado_sinAutenticacion_devuelve401() {
        given().contentType(ContentType.JSON)
                .body(new CambiarEstadoRequest(false))
                .when().patch("/productos/1/estado")
                .then().statusCode(401);
    }

    @Test
    void cambiarEstado_comoVendedorSinPermisoProductoEliminar_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        ProductoResponse creado = ProductoTestHelper.crearProductoConCategoriaNueva(admin.accessToken(), BigDecimal.TEN);
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/productos/{id}/estado", creado.id())
                .then().statusCode(403);
    }
}
