package com.eldiamante360.auth.presentation;

import com.eldiamante360.auth.presentation.dto.request.ActualizarUsuarioRequest;
import com.eldiamante360.auth.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.auth.presentation.dto.request.CambiarPasswordRequest;
import com.eldiamante360.auth.presentation.dto.request.CrearUsuarioRequest;
import com.eldiamante360.auth.presentation.dto.response.RestablecerPasswordResponse;
import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.auth.presentation.dto.response.UsuarioResponse;
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
 * Pruebas de integracion de {@link UsuarioController}: CRUD de usuarios,
 * cambio de estado, cambio de contrasena propia y restablecimiento de
 * contrasena por un administrador.
 */
class UsuarioControllerIT extends BaseIntegrationTest {

    private static final Long ROL_VENDEDOR_ID_PLACEHOLDER = -1L; // se resuelve en runtime via AuthTestHelper

    // ---------------------------------------------------------------
    // POST /usuarios
    // ---------------------------------------------------------------

    @Test
    void crear_datosValidos_devuelve201ConUsuarioActivoYDebeCambiarPasswordEnTrue() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolVendedorId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");
        String username = TestDataFactory.username("it_crear");

        UsuarioResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(username, TestDataFactory.passwordValida(),
                        TestDataFactory.nombreCompleto("Crear OK"), rolVendedorId))
                .when().post("/usuarios")
                .then().statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(UsuarioResponse.class);

        assertThat(creado.id()).isNotNull();
        assertThat(creado.username()).isEqualTo(username);
        assertThat(creado.rolId()).isEqualTo(rolVendedorId);
        assertThat(creado.rolNombre()).isEqualTo("VENDEDOR");
        assertThat(creado.activo()).isTrue();
        assertThat(creado.debeCambiarPassword()).isTrue();
        assertThat(creado.ultimoLogin()).isNull();
    }

    @Test
    void crear_usernameDuplicado_devuelve409() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");
        String username = TestDataFactory.username("it_dup");
        var request = new CrearUsuarioRequest(username, TestDataFactory.passwordValida(), "Original", rolId);

        AuthTestHelper.autenticado(admin.accessToken()).body(request)
                .when().post("/usuarios").then().statusCode(201);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(username, TestDataFactory.passwordValida(), "Duplicado", rolId))
                .when().post("/usuarios")
                .then().statusCode(409);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 409);
        assertThat(error.message()).contains(username).contains("ya esta en uso");
    }

    @Test
    void crear_rolInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(TestDataFactory.username("it_rolx"), TestDataFactory.passwordValida(), "Sin rol", 999_999L))
                .when().post("/usuarios")
                .then().statusCode(404);
    }

    @Test
    void crear_camposInvalidos_devuelve400ConUnErrorPorCampo() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest("ab", "corta", "", null)) // username <3, password sin numero/corta, nombre vacio, rol nulo
                .when().post("/usuarios")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "username");
        ApiErrorAssertions.verificarErrorDeCampo(error, "password");
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombreCompleto", "El nombre completo es obligatorio");
        ApiErrorAssertions.verificarErrorDeCampo(error, "rolId", "El rol es obligatorio");
    }

    @Test
    void crear_usernameConCaracteresNoPermitidos_devuelve400() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest("usuario con espacios!", TestDataFactory.passwordValida(), "Nombre", rolId))
                .when().post("/usuarios")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "username",
                "El username solo puede contener letras, numeros, puntos, guiones y guion bajo");
    }

    @Test
    void crear_sinAutenticacion_devuelve401() {
        given().contentType(ContentType.JSON)
                .body(new CrearUsuarioRequest("x", "x", "x", 1L))
                .when().post("/usuarios")
                .then().statusCode(401);
    }

    @Test
    void crear_comoVendedorSinPermisoUsuarioCrear_devuelve403() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new CrearUsuarioRequest(TestDataFactory.username("it_noauth"), TestDataFactory.passwordValida(), "X", 1L))
                .when().post("/usuarios")
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // GET /usuarios, GET /usuarios/{id}
    // ---------------------------------------------------------------

    @Test
    void listar_comoAdmin_devuelvePaginaConFormaEstandar() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        PageResponse<UsuarioResponse> pagina = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/usuarios")
                .then().statusCode(200)
                .extract().as(new TypeRef<>() {});

        assertThat(pagina.pagina()).isEqualTo(0);
        assertThat(pagina.contenido()).isNotEmpty(); // al menos jhon/angie sembrados
        assertThat(pagina.totalElementos()).isGreaterThanOrEqualTo(pagina.contenido().size());
        assertThat(pagina.contenido()).extracting(UsuarioResponse::username).contains(AuthTestHelper.ADMIN_USERNAME);
    }

    @Test
    void listar_sinPermisoUsuarioLeer_devuelve403() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/usuarios")
                .then().statusCode(403);
    }

    @Test
    void obtener_idExistente_devuelveElUsuarioCreado() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");
        UsuarioResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(TestDataFactory.username("it_obtener"), TestDataFactory.passwordValida(),
                        "Para Obtener", rolId))
                .when().post("/usuarios").then().statusCode(201).extract().as(UsuarioResponse.class);

        UsuarioResponse obtenido = AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/usuarios/{id}", creado.id())
                .then().statusCode(200)
                .extract().as(UsuarioResponse.class);

        assertThat(obtenido).isEqualTo(creado);
    }

    @Test
    void obtener_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/usuarios/{id}", 999_999_999L)
                .then().statusCode(404);
    }

    @Test
    void obtener_idConFormatoInvalido_devuelve500EnVezDe400_knownIssue() {
        // KNOWN ISSUE: {id} se bindea como Long en el path variable. Un valor no
        // numerico dispara MethodArgumentTypeMismatchException, para la cual
        // GlobalExceptionHandler NO tiene @ExceptionHandler especifico -> cae al
        // handler generico -> 500 en vez de un 400 "id invalido". Este patron se
        // repite en todos los endpoints con {id} numerico de la API (no es
        // exclusivo de este controller); se documenta aqui como representante.
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        AuthTestHelper.autenticado(admin.accessToken())
                .when().get("/usuarios/{id}", "no-es-un-numero")
                .then().statusCode(500);
    }

    // ---------------------------------------------------------------
    // PUT /usuarios/{id}
    // ---------------------------------------------------------------

    @Test
    void actualizar_datosValidos_persisteNombreYRolNuevos() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolVendedorId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");
        Long rolAdminId = AuthTestHelper.obtenerRolIdPorNombre("ADMIN");
        UsuarioResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(TestDataFactory.username("it_actualizar"), TestDataFactory.passwordValida(),
                        "Nombre Original", rolVendedorId))
                .when().post("/usuarios").then().statusCode(201).extract().as(UsuarioResponse.class);

        String nuevoNombre = TestDataFactory.nombreCompleto("Nombre Actualizado");
        UsuarioResponse actualizado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new ActualizarUsuarioRequest(nuevoNombre, rolAdminId))
                .when().put("/usuarios/{id}", creado.id())
                .then().statusCode(200)
                .extract().as(UsuarioResponse.class);

        assertThat(actualizado.nombreCompleto()).isEqualTo(nuevoNombre);
        assertThat(actualizado.rolId()).isEqualTo(rolAdminId);
        assertThat(actualizado.rolNombre()).isEqualTo("ADMIN");
        assertThat(actualizado.username()).isEqualTo(creado.username()); // el username no es editable por este endpoint
    }

    @Test
    void actualizar_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");

        AuthTestHelper.autenticado(admin.accessToken())
                .body(new ActualizarUsuarioRequest("Nombre", rolId))
                .when().put("/usuarios/{id}", 999_999_999L)
                .then().statusCode(404);
    }

    @Test
    void actualizar_camposInvalidos_devuelve400() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");
        UsuarioResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(TestDataFactory.username("it_actinv"), TestDataFactory.passwordValida(), "N", rolId))
                .when().post("/usuarios").then().statusCode(201).extract().as(UsuarioResponse.class);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new ActualizarUsuarioRequest("", null))
                .when().put("/usuarios/{id}", creado.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "nombreCompleto", "El nombre completo es obligatorio");
        ApiErrorAssertions.verificarErrorDeCampo(error, "rolId", "El rol es obligatorio");
    }

    @Test
    void actualizar_sinPermisoUsuarioEditar_devuelve403() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new ActualizarUsuarioRequest("X", 1L))
                .when().put("/usuarios/{id}", 1L)
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // PATCH /usuarios/{id}/estado
    // ---------------------------------------------------------------

    @Test
    void cambiarEstado_desactivarYReactivar_reflejaElCambio() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");
        UsuarioResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(TestDataFactory.username("it_estado"), TestDataFactory.passwordValida(), "N", rolId))
                .when().post("/usuarios").then().statusCode(201).extract().as(UsuarioResponse.class);
        assertThat(creado.activo()).isTrue();

        UsuarioResponse desactivado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/usuarios/{id}/estado", creado.id())
                .then().statusCode(200)
                .extract().as(UsuarioResponse.class);
        assertThat(desactivado.activo()).isFalse();

        UsuarioResponse reactivado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(true))
                .when().patch("/usuarios/{id}/estado", creado.id())
                .then().statusCode(200)
                .extract().as(UsuarioResponse.class);
        assertThat(reactivado.activo()).isTrue();
    }

    @Test
    void cambiarEstado_desactivarPropioUsuario_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/usuarios/{id}/estado", admin.usuario().id())
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).isEqualTo("No puede desactivar su propio usuario");
    }

    @Test
    void cambiarEstado_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/usuarios/{id}/estado", 999_999_999L)
                .then().statusCode(404);
    }

    @Test
    void cambiarEstado_campoActivoFaltante_devuelve400() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");
        UsuarioResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(TestDataFactory.username("it_estadoinv"), TestDataFactory.passwordValida(), "N", rolId))
                .when().post("/usuarios").then().statusCode(201).extract().as(UsuarioResponse.class);

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .body("{}")
                .when().patch("/usuarios/{id}/estado", creado.id())
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "activo", "El campo activo es obligatorio");
    }

    @Test
    void cambiarEstado_sinPermisoUsuarioEliminar_devuelve403() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/usuarios/{id}/estado", 1L)
                .then().statusCode(403);
    }

    // ---------------------------------------------------------------
    // PATCH /usuarios/me/password
    // ---------------------------------------------------------------

    @Test
    void cambiarMiPassword_passwordActualCorrecta_devuelve204YPermiteLoginConLaNueva() {
        TokenResponse vendedorInicial = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");
        // AuthTestHelper.crearUsuarioYLogin no expone la password en texto plano usada al crear,
        // asi que recreamos el flujo aqui manualmente para conocerla.
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");
        String username = TestDataFactory.username("it_cambiopw");
        String passwordOriginal = TestDataFactory.passwordValida();
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(username, passwordOriginal, "Cambia Password", rolId))
                .when().post("/usuarios").then().statusCode(201);
        TokenResponse sesion = AuthTestHelper.login(username, passwordOriginal);

        String passwordNueva = TestDataFactory.passwordValida();
        AuthTestHelper.autenticado(sesion.accessToken())
                .body(new CambiarPasswordRequest(passwordOriginal, passwordNueva))
                .when().patch("/usuarios/me/password")
                .then().statusCode(204);

        AuthTestHelper.login(username, passwordNueva); // no lanza si el login es exitoso (200 esperado)
        AuthTestHelper.loginRaw(username, passwordOriginal).then().statusCode(401); // la vieja ya no sirve
    }

    @Test
    void cambiarMiPassword_passwordActualIncorrecta_devuelve422() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        var respuesta = AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new CambiarPasswordRequest("password-actual-incorrecta", TestDataFactory.passwordValida()))
                .when().patch("/usuarios/me/password")
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).isEqualTo("La contrasena actual ingresada no es correcta");
    }

    @Test
    void cambiarMiPassword_passwordNuevaNoCumpleFormato_devuelve400() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        var respuesta = AuthTestHelper.autenticado(vendedor.accessToken())
                .body(new CambiarPasswordRequest("lo-que-sea", "corta"))
                .when().patch("/usuarios/me/password")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "passwordNueva");
    }

    @Test
    void cambiarMiPassword_sinAutenticacion_devuelve401() {
        given().contentType(ContentType.JSON)
                .body(new CambiarPasswordRequest("a", "Passw0rd1"))
                .when().patch("/usuarios/me/password")
                .then().statusCode(401);
    }

    @Test
    void cambiarMiPassword_noRequierePermisosEspeciales_cualquierUsuarioAutenticadoPuede() {
        // A diferencia de crear/editar/desactivar, este endpoint NO tiene @PreAuthorize:
        // cualquier usuario autenticado puede cambiar SU PROPIA contrasena, sin importar su rol/permisos.
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");
        String username = TestDataFactory.username("it_selfpw");
        String passwordOriginal = TestDataFactory.passwordValida();
        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(username, passwordOriginal, "Self Password", rolId))
                .when().post("/usuarios").then().statusCode(201);
        TokenResponse sesion = AuthTestHelper.login(username, passwordOriginal);

        AuthTestHelper.autenticado(sesion.accessToken())
                .body(new CambiarPasswordRequest(passwordOriginal, TestDataFactory.passwordValida()))
                .when().patch("/usuarios/me/password")
                .then().statusCode(204); // VENDEDOR no tiene ningun permiso USUARIO_*, y aun asi puede.
    }

    // ---------------------------------------------------------------
    // PATCH /usuarios/{id}/password  (restablecer por un administrador)
    // ---------------------------------------------------------------

    @Test
    void restablecerPassword_comoAdmin_generaPasswordTemporalYFuerzaCambioEnProximoLogin() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        Long rolId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");
        String username = TestDataFactory.username("it_reset");
        UsuarioResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(username, TestDataFactory.passwordValida(), "Reset Me", rolId))
                .when().post("/usuarios").then().statusCode(201).extract().as(UsuarioResponse.class);

        RestablecerPasswordResponse resultado = AuthTestHelper.autenticado(admin.accessToken())
                .when().patch("/usuarios/{id}/password", creado.id())
                .then().statusCode(200)
                .extract().as(RestablecerPasswordResponse.class);

        assertThat(resultado.id()).isEqualTo(creado.id());
        assertThat(resultado.username()).isEqualTo(username);
        assertThat(resultado.passwordTemporal()).isNotBlank();
        assertThat(resultado.passwordTemporal()).matches(".*[A-Za-z].*").matches(".*\\d.*"); // letra + digito, ver GeneradorPasswordTemporal

        // La password temporal permite iniciar sesion...
        TokenResponse sesionConTemporal = AuthTestHelper.login(username, resultado.passwordTemporal());
        assertThat(sesionConTemporal.usuario().debeCambiarPassword()).isTrue(); // ...y obliga a cambiarla.
    }

    @Test
    void restablecerPassword_sobreSiMismo_devuelve422() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(admin.accessToken())
                .when().patch("/usuarios/{id}/password", admin.usuario().id())
                .then().statusCode(422);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 422);
        assertThat(error.message()).contains("No puede restablecer su propia contrasena");
    }

    @Test
    void restablecerPassword_idInexistente_devuelve404() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        AuthTestHelper.autenticado(admin.accessToken())
                .when().patch("/usuarios/{id}/password", 999_999_999L)
                .then().statusCode(404);
    }

    @Test
    void restablecerPassword_sinPermisoUsuarioEditar_devuelve403() {
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().patch("/usuarios/{id}/password", 1L)
                .then().statusCode(403);
    }
}
