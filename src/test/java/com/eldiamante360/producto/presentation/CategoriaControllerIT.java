package com.eldiamante360.producto.presentation;

import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.producto.presentation.dto.request.ActualizarCategoriaRequest;
import com.eldiamante360.producto.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.producto.presentation.dto.request.CrearCategoriaRequest;
import com.eldiamante360.producto.presentation.dto.response.CategoriaResponse;
import com.eldiamante360.shared.it.ApiErrorAssertions;
import com.eldiamante360.shared.it.AuthTestHelper;
import com.eldiamante360.shared.it.BaseIntegrationTest;
import com.eldiamante360.shared.it.TestDataFactory;
import com.eldiamante360.shared.presentation.ApiErrorResponse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integracion de {@link CategoriaController}.
 *
 * <p><b>Nota sobre permisos:</b> el modulo Productos y Categorias usa un
 * unico conjunto de 4 permisos (PRODUCTO_CREAR, PRODUCTO_LEER,
 * PRODUCTO_EDITAR, PRODUCTO_ELIMINAR) compartido entre Categoria y Producto
 * (ver V1__create_seguridad.sql); no existen permisos CATEGORIA_* separados.
 * El rol VENDEDOR sembrado solo tiene PRODUCTO_LEER, por lo que puede listar
 * categorias pero recibe 403 al intentar crearlas, editarlas o cambiarles
 * el estado.
 */
class CategoriaControllerIT extends BaseIntegrationTest {

    // ---------------------------------------------------------------
    // POST /categorias
    // ---------------------------------------------------------------

    @Test
    void crear_datosValidos_devuelve201YCuerpo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearCategoriaRequest(TestDataFactory.nombreCompleto("Categoria IT"), "Descripcion de prueba");

        CategoriaResponse creada = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/categorias")
                .then().statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(CategoriaResponse.class);

        assertThat(creada.id()).isNotNull();
        assertThat(creada.nombre()).isEqualTo(request.nombre());
        assertThat(creada.descripcion()).isEqualTo(request.descripcion());
        assertThat(creada.activo()).isTrue();
    }

    @Test
    void crear_sinDescripcion_devuelve201ConDescripcionNula() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearCategoriaRequest(TestDataFactory.nombreCompleto("Categoria Sin Desc"), null);

        CategoriaResponse creada = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/categorias")
                .then().statusCode(201)
                .extract().as(CategoriaResponse.class);

        assertThat(creada.descripcion()).isNull();
    }

    @Test
    void crear_nombreDuplicado_devuelve409() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse existente = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new CrearCategoriaRequest(existente.nombre(), "Otra descripcion");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/categorias")
                .then().statusCode(409);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 409);
        assertThat(error.message()).contains(existente.nombre());
    }

    @Test
    void crear_nombreVacio_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearCategoriaRequest("", "Descripcion");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/categorias")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombre");
    }

    @Test
    void crear_nombreExcedeLongitudMaxima_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearCategoriaRequest("N".repeat(61), "Descripcion");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/categorias")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombre");
    }

    @Test
    void crear_descripcionExcedeLongitudMaxima_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new CrearCategoriaRequest(TestDataFactory.nombreCompleto("Categoria Desc Larga"), "D".repeat(151));

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().post("/categorias")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "descripcion");
    }

    @Test
    void crear_sinAutenticacion_devuelve401() {
        var request = new CrearCategoriaRequest(TestDataFactory.nombreCompleto("Categoria Sin Auth"), "Descripcion");

        given().contentType(ContentType.JSON).body(request)
                .when().post("/categorias")
                .then().statusCode(401);
    }

    @Test
    void crear_comoVendedorSinPermisoProductoCrear_devuelve403() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");
        var request = new CrearCategoriaRequest(TestDataFactory.nombreCompleto("Categoria Vendedor"), "Descripcion");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(request)
                .when().post("/categorias")
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // GET /categorias
    // ---------------------------------------------------------------

    @Test
    void listar_devuelveListaConLaCategoriaCreada() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse creada = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        List<CategoriaResponse> categorias = List.of(AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/categorias")
                .then().statusCode(200)
                .extract().as(CategoriaResponse[].class));

        assertThat(categorias).extracting(CategoriaResponse::id).contains(creada.id());
    }

    @Test
    void listar_sinAutenticacion_devuelve401() {
        given().when().get("/categorias")
                .then().statusCode(401);
    }

    @Test
    void listar_comoVendedorConPermisoProductoLeer_devuelve200() {
        // El rol VENDEDOR sembrado tiene PRODUCTO_LEER (ver Javadoc de clase),
        // por lo que no hay forma de probar un 403 en este endpoint con los
        // datos sembrados actuales; se documenta el acceso permitido.
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/categorias")
                .then().statusCode(200);
    }

    // ---------------------------------------------------------------
    // PUT /categorias/{id}
    // ---------------------------------------------------------------

    @Test
    void actualizar_datosValidos_devuelve200ConDatosActualizados() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse creada = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new ActualizarCategoriaRequest(TestDataFactory.nombreCompleto("Categoria Actualizada"), "Nueva descripcion");

        CategoriaResponse actualizada = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/categorias/{id}", creada.id())
                .then().statusCode(200)
                .extract().as(CategoriaResponse.class);

        assertThat(actualizada.id()).isEqualTo(creada.id());
        assertThat(actualizada.nombre()).isEqualTo(request.nombre());
        assertThat(actualizada.descripcion()).isEqualTo(request.descripcion());
        assertThat(actualizada.activo()).isTrue();
    }

    @Test
    void actualizar_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        var request = new ActualizarCategoriaRequest(TestDataFactory.nombreCompleto("Categoria"), "Descripcion");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/categorias/999999999")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void actualizar_nombreVacio_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse creada = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new ActualizarCategoriaRequest("", "Descripcion");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/categorias/{id}", creada.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombre");
    }

    /**
     * Known Issue: a diferencia de {@code POST /categorias} (que rechaza
     * nombres duplicados con 409, ver {@code CrearCategoriaService}),
     * {@code PUT /categorias/{id}} (ver {@code ActualizarCategoriaService})
     * NO valida duplicados de nombre contra otras categorias antes de
     * guardar -- solo llama a {@code categoria.actualizarDatos(...)} y
     * guarda, sin consultar {@code existePorNombre}. La tabla si tiene una
     * restriccion UNIQUE a nivel de base de datos
     * ({@code uq_categoria_producto_nombre}, ver V2__create_producto_inventario.sql),
     * asi que el intento de duplicado no se guarda silenciosamente, pero
     * tampoco se traduce a un 409 controlado: {@link GlobalExceptionHandler}
     * no tiene un {@code @ExceptionHandler} para
     * {@code DataIntegrityViolationException}, por lo que cae en el handler
     * generico de {@code Exception} y responde <b>500</b> ("Ocurrio un error
     * inesperado") en vez de 409. Se documenta el comportamiento real (500)
     * en vez de el 409/200 que seria deseable; no se modifica produccion.
     */
    @Test
    void actualizar_nombreDuplicadoDeOtraCategoria_devuelve500EnVezDe409_KnownIssue() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse categoriaA = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());
        CategoriaResponse categoriaB = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var request = new ActualizarCategoriaRequest(categoriaA.nombre(), "Descripcion duplicada a proposito");

        AuthTestHelper.autenticado(admin.accessToken())
                .body(request)
                .when().put("/categorias/{id}", categoriaB.id())
                .then().statusCode(500);
    }

    @Test
    void actualizar_sinAutenticacion_devuelve401() {
        var request = new ActualizarCategoriaRequest(TestDataFactory.nombreCompleto("Categoria"), "Descripcion");

        given().contentType(ContentType.JSON).body(request)
                .when().put("/categorias/1")
                .then().statusCode(401);
    }

    @Test
    void actualizar_comoVendedorSinPermisoProductoEditar_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse creada = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        var request = new ActualizarCategoriaRequest(TestDataFactory.nombreCompleto("Categoria"), "Descripcion");
        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(request)
                .when().put("/categorias/{id}", creada.id())
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // PATCH /categorias/{id}/estado
    // ---------------------------------------------------------------

    @Test
    void cambiarEstado_desactivar_devuelve200ConActivoFalse() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse creada = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        CategoriaResponse actualizada = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/categorias/{id}/estado", creada.id())
                .then().statusCode(200)
                .extract().as(CategoriaResponse.class);

        assertThat(actualizada.activo()).isFalse();
    }

    @Test
    void cambiarEstado_reactivar_devuelve200ConActivoTrue() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse creada = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/categorias/{id}/estado", creada.id())
                .then().statusCode(200);

        CategoriaResponse actualizada = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(true))
                .when().patch("/categorias/{id}/estado", creada.id())
                .then().statusCode(200)
                .extract().as(CategoriaResponse.class);

        assertThat(actualizada.activo()).isTrue();
    }

    @Test
    void cambiarEstado_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/categorias/999999999/estado")
                .then().statusCode(404);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 404);
    }

    @Test
    void cambiarEstado_activoNulo_devuelve400ConErrorDeCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse creada = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(null))
                .when().patch("/categorias/{id}/estado", creada.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "activo");
    }

    @Test
    void cambiarEstado_sinAutenticacion_devuelve401() {
        given().contentType(ContentType.JSON).body(new CambiarEstadoRequest(false))
                .when().patch("/categorias/1/estado")
                .then().statusCode(401);
    }

    @Test
    void cambiarEstado_comoVendedorSinPermisoProductoEliminar_devuelve403() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        CategoriaResponse creada = ProductoTestHelper.crearCategoriaActiva(admin.accessToken());
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/categorias/{id}/estado", creada.id())
                .then().statusCode(403);
    }
}
