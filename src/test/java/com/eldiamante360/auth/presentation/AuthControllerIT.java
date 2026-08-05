package com.eldiamante360.auth.presentation;

import com.eldiamante360.auth.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.auth.presentation.dto.request.CrearUsuarioRequest;
import com.eldiamante360.auth.presentation.dto.request.LoginRequest;
import com.eldiamante360.auth.presentation.dto.request.RefreshTokenRequest;
import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.auth.presentation.dto.response.UsuarioResponse;
import com.eldiamante360.auth.presentation.dto.response.UsuarioSesionResponse;
import com.eldiamante360.shared.it.ApiErrorAssertions;
import com.eldiamante360.shared.it.AuthTestHelper;
import com.eldiamante360.shared.it.BaseIntegrationTest;
import com.eldiamante360.shared.it.TestDataFactory;
import com.eldiamante360.shared.presentation.ApiErrorResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integracion de {@link AuthController}: login, refresh,
 * logout y /me contra un servidor real (RestAssured + Testcontainers).
 *
 * <p>IMPORTANTE: estas pruebas NO modifican la contrasena ni el estado del
 * usuario administrador sembrado "jhon" (usado como credencial de lectura
 * en {@link AuthTestHelper#loginComoAdmin()} en toda la suite). Cualquier
 * prueba que necesite crear/desactivar/mutar un usuario usa un usuario de
 * prueba desechable creado via {@link AuthTestHelper#crearUsuarioYLogin}.
 */
class AuthControllerIT extends BaseIntegrationTest {

    // ---------------------------------------------------------------
    // POST /auth/login
    // ---------------------------------------------------------------

    @Test
    void login_credencialesValidasDeAdminSembrado_devuelveTokensYDatosDeUsuario() {
        TokenResponse respuesta = AuthTestHelper.loginComoAdmin();

        assertThat(respuesta.accessToken()).isNotBlank();
        assertThat(respuesta.accessToken().split("\\.")).hasSize(3); // header.payload.signature
        assertThat(respuesta.refreshToken()).isNotBlank();
        assertThat(respuesta.expiraEnSegundos()).isEqualTo(15 * 60L); // app.jwt.access-token-expiration-minutes default = 15

        UsuarioSesionResponse usuario = respuesta.usuario();
        assertThat(usuario.username()).isEqualTo(AuthTestHelper.ADMIN_USERNAME);
        assertThat(usuario.rol()).isEqualTo("ADMIN");
        assertThat(usuario.permisos()).isNotEmpty().contains("USUARIO_CREAR", "USUARIO_LEER", "USUARIO_EDITAR");
        assertThat(usuario.id()).isNotNull();
        // Sembrado por AdminUserSeeder con debeCambiarPassword=true; esta prueba nunca cambia la password de jhon.
        assertThat(usuario.debeCambiarPassword()).isTrue();
    }

    @Test
    void login_passwordIncorrecta_devuelve401ConMensajeGenerico() {
        var respuesta = AuthTestHelper.loginRaw(AuthTestHelper.ADMIN_USERNAME, "password-incorrecta-123")
                .then().statusCode(401);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 401);
        // Regla de seguridad: el mensaje no distingue "usuario no existe" de "password incorrecta".
        assertThat(error.message()).isEqualTo("Usuario o contrasena invalidos");
    }

    @Test
    void login_usuarioInexistente_devuelve401ConElMismoMensajeGenericoQuePasswordIncorrecta() {
        var respuesta = AuthTestHelper.loginRaw("usuario-que-no-existe-" + TestDataFactory.sufijoUnico(), "cualquier-cosa")
                .then().statusCode(401);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 401);
        assertThat(error.message()).isEqualTo("Usuario o contrasena invalidos");
    }

    @Test
    void login_camposVacios_devuelve400ConErroresDeValidacionParaAmbosCampos() {
        var respuesta = given()
                .contentType(ContentType.JSON)
                .body(new LoginRequest("", ""))
                .when().post("/auth/login")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "username", "El usuario es obligatorio");
        ApiErrorAssertions.verificarErrorDeCampo(error, "password", "La contrasena es obligatoria");
    }

    @Test
    void login_metodoHttpNoSoportado_devuelve405() {
        given().contentType(ContentType.JSON)
                .when().get("/auth/login")
                .then().statusCode(405);
    }

    @Test
    void login_usuarioInactivo_devuelve500EnVezDeUnCodigoDeAutenticacion_knownIssue() {
        // KNOWN ISSUE: UsuarioInactivoException (lanzada por Usuario.verificarPuedeAutenticar())
        // extiende DomainException directamente y NO tiene @ExceptionHandler propio en
        // GlobalExceptionHandler (solo CredencialesInvalidasException y RefreshTokenInvalidoException
        // estan mapeados a 401 desde ese paquete). Cae al handler generico -> 500.
        // Comportamiento esperable/deseable seria 401 o 422, pero esta prueba documenta el
        // comportamiento ACTUAL del sistema (no se modifica codigo de produccion en esta tarea).
        TokenResponse admin = AuthTestHelper.loginComoAdmin();
        String username = TestDataFactory.username("it_inactivo");
        String password = TestDataFactory.passwordValida();
        Long rolId = AuthTestHelper.obtenerRolIdPorNombre("VENDEDOR");

        UsuarioResponse creado = AuthTestHelper.autenticado(admin.accessToken())
                .body(new CrearUsuarioRequest(username, password, TestDataFactory.nombreCompleto("IT Inactivo"), rolId))
                .when().post("/usuarios")
                .then().statusCode(201)
                .extract().as(UsuarioResponse.class);

        AuthTestHelper.autenticado(admin.accessToken())
                .body(new CambiarEstadoRequest(false))
                .when().patch("/usuarios/{id}/estado", creado.id())
                .then().statusCode(200);

        AuthTestHelper.loginRaw(username, password)
                .then().statusCode(500); // Comportamiento actual documentado como Known Issue.
    }

    // ---------------------------------------------------------------
    // POST /auth/refresh
    // ---------------------------------------------------------------

    @Test
    void refresh_conTokenVigente_rotaTokensYDevuelveNuevasCredenciales() {
        TokenResponse sesionInicial = AuthTestHelper.loginComoAdmin();

        var respuesta = given()
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(sesionInicial.refreshToken()))
                .when().post("/auth/refresh")
                .then().statusCode(200)
                .extract().as(TokenResponse.class);

        // No se compara respuesta.accessToken() contra sesionInicial.accessToken() por
        // igualdad estricta: los claims "iat"/"exp" del JWT se serializan con precision
        // de segundos (ver JwtService, Date.from(Instant)) y la firma HMAC es determinista,
        // asi que dos tokens con exactamente los mismos claims generados dentro del mismo
        // segundo de reloj son byte-a-byte identicos. Eso no es un bug: el contenido del
        // token (claims) es lo que importa, no el string. Lo que si debe rotar siempre es
        // el refresh token (tiene un componente aleatorio propio), que es lo que en la
        // practica habilita/objeta la deteccion de reuso.
        assertThat(respuesta.accessToken()).isNotBlank();
        assertThat(respuesta.refreshToken()).isNotBlank().isNotEqualTo(sesionInicial.refreshToken());
        assertThat(respuesta.usuario().username()).isEqualTo(AuthTestHelper.ADMIN_USERNAME);
    }

    @Test
    void refresh_reutilizandoUnTokenYaRotado_esRechazado() {
        TokenResponse sesionInicial = AuthTestHelper.loginComoAdmin();

        TokenResponse sesionRotada = given()
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(sesionInicial.refreshToken()))
                .when().post("/auth/refresh")
                .then().statusCode(200)
                .extract().as(TokenResponse.class);

        // Reutilizar el refresh token original (ya rotado/revocado) debe ser rechazado...
        var respuestaReuso = given()
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(sesionInicial.refreshToken()))
                .when().post("/auth/refresh")
                .then().statusCode(401);
        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuestaReuso, 401);
        assertThat(error.message()).containsIgnoringCase("reutilizacion");
    }

    @Test
    void refresh_deteccionDeReusoNoRevocaRealmenteLasDemasSesionesDelUsuario_knownIssue() {
        // KNOWN ISSUE (seguridad): el mensaje de error de RefreshTokenService
        // ("...todas las sesiones fueron cerradas") promete que, ante la
        // reutilizacion de un refresh token ya rotado, TODAS las sesiones del
        // usuario quedan revocadas (revocarTodosPorUsuario). En la practica esto
        // NO ocurre: revocarTodosPorUsuario() y el throw de
        // RefreshTokenInvalidoException ocurren dentro del MISMO metodo
        // @Transactional; como esa excepcion es un RuntimeException, Spring hace
        // rollback de toda la transaccion al propagarla -- incluyendo el UPDATE
        // masivo de revocacion, aunque @Modifying(flushAutomatically = true) lo
        // haya enviado al connection JDBC (flush no es commit). Resultado neto:
        // el token "hermano" emitido en la rotacion (sesionRotada) sigue
        // funcionando con total normalidad. Esta prueba documenta el
        // comportamiento ACTUAL (no se modifica codigo de produccion aqui).
        TokenResponse sesionInicial = AuthTestHelper.loginComoAdmin();

        TokenResponse sesionRotada = given()
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(sesionInicial.refreshToken()))
                .when().post("/auth/refresh")
                .then().statusCode(200)
                .extract().as(TokenResponse.class);

        // Dispara la deteccion de reuso (y, en teoria, la revocacion masiva).
        given()
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(sesionInicial.refreshToken()))
                .when().post("/auth/refresh")
                .then().statusCode(401);

        // Comportamiento actual: sesionRotada NO fue revocada pese al mensaje de error.
        given()
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(sesionRotada.refreshToken()))
                .when().post("/auth/refresh")
                .then().statusCode(200);
    }

    @Test
    void refresh_tokenInexistenteOInventado_devuelve401() {
        var respuesta = given()
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest("token-opaco-que-nunca-fue-emitido-" + TestDataFactory.sufijoUnico()))
                .when().post("/auth/refresh")
                .then().statusCode(401);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 401);
        assertThat(error.message()).isEqualTo("Refresh token invalido");
    }

    @Test
    void refresh_campoVacio_devuelve400() {
        var respuesta = given()
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(""))
                .when().post("/auth/refresh")
                .then().statusCode(400);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
        ApiErrorAssertions.verificarErrorDeCampo(error, "refreshToken", "El refresh token es obligatorio");
    }

    // ---------------------------------------------------------------
    // POST /auth/logout
    // ---------------------------------------------------------------

    @Test
    void logout_requiereAccessTokenValido_sinAutenticacionDevuelve401() {
        // A diferencia de login/refresh, /auth/logout SI exige un access token vigente
        // (no esta en la lista permitAll de SecurityConfig), aunque el cuerpo solo
        // contenga el refresh token. Regla inferida y documentada explicitamente aqui.
        var respuesta = given()
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest("cualquier-refresh-token"))
                .when().post("/auth/logout")
                .then().statusCode(401);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 401);
    }

    @Test
    void logout_conTokenVigente_revocaElRefreshTokenYNoPermiteReutilizarlo() {
        TokenResponse sesion = AuthTestHelper.loginComoAdmin();

        AuthTestHelper.autenticado(sesion.accessToken())
                .body(new RefreshTokenRequest(sesion.refreshToken()))
                .when().post("/auth/logout")
                .then().statusCode(204);

        given()
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(sesion.refreshToken()))
                .when().post("/auth/refresh")
                .then().statusCode(401);
    }

    @Test
    void logout_conRefreshTokenInexistente_esIdempotenteYDevuelve204() {
        // Regla inferida: LogoutService hace un no-op silencioso si el hash no existe
        // (no revela si el token era valido o no), por lo que sigue respondiendo 204.
        TokenResponse sesion = AuthTestHelper.loginComoAdmin();

        AuthTestHelper.autenticado(sesion.accessToken())
                .body(new RefreshTokenRequest("token-que-nunca-existio-" + TestDataFactory.sufijoUnico()))
                .when().post("/auth/logout")
                .then().statusCode(204);
    }

    @Test
    void logout_campoVacio_devuelve400() {
        TokenResponse sesion = AuthTestHelper.loginComoAdmin();

        var respuesta = AuthTestHelper.autenticado(sesion.accessToken())
                .body(new RefreshTokenRequest(""))
                .when().post("/auth/logout")
                .then().statusCode(400);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 400);
    }

    @Test
    void logout_noValidaQueElRefreshTokenPerteneceAlUsuarioAutenticado_knownIssueMenor() {
        // KNOWN ISSUE (menor): LogoutService revoca por hash sin comparar
        // RefreshToken.usuarioId contra el id del usuario autenticado. Cualquier
        // usuario autenticado que conozca el valor en texto plano de un refresh
        // token ajeno puede revocarlo. El riesgo practico es bajo (el valor es un
        // secreto opaco de alta entropia, no adivinable), pero se documenta el
        // comportamiento actual explicitamente.
        TokenResponse sesionAdmin = AuthTestHelper.loginComoAdmin();
        TokenResponse sesionVendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(sesionVendedor.accessToken())
                .body(new RefreshTokenRequest(sesionAdmin.refreshToken()))
                .when().post("/auth/logout")
                .then().statusCode(204);

        given()
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(sesionAdmin.refreshToken()))
                .when().post("/auth/refresh")
                .then().statusCode(401); // El refresh token del admin quedo revocado por otro usuario.
    }

    // ---------------------------------------------------------------
    // GET /auth/me
    // ---------------------------------------------------------------

    @Test
    void me_conTokenValido_devuelveDatosDeSesionConsistentesConElLogin() {
        TokenResponse sesion = AuthTestHelper.loginComoAdmin();

        UsuarioSesionResponse me = AuthTestHelper.autenticado(sesion.accessToken())
                .when().get("/auth/me")
                .then().statusCode(200)
                .extract().as(UsuarioSesionResponse.class);

        assertThat(me.id()).isEqualTo(sesion.usuario().id());
        assertThat(me.username()).isEqualTo(sesion.usuario().username());
        assertThat(me.rol()).isEqualTo(sesion.usuario().rol());
        assertThat(me.permisos()).isEqualTo(sesion.usuario().permisos());
        assertThat(me.debeCambiarPassword()).isEqualTo(sesion.usuario().debeCambiarPassword());
    }

    @Test
    void me_sinHeaderAuthorization_devuelve401() {
        var respuesta = given()
                .when().get("/auth/me")
                .then().statusCode(401);

        ApiErrorResponse error = ApiErrorAssertions.verificarFormaEstandar(respuesta, 401);
        assertThat(error.message()).containsIgnoringCase("autenticacion");
    }

    @Test
    void me_conTokenMalformado_devuelve401() {
        var respuesta = given()
                .header("Authorization", "Bearer esto-no-es-un-jwt-valido")
                .when().get("/auth/me")
                .then().statusCode(401);

        ApiErrorAssertions.verificarFormaEstandar(respuesta, 401);
    }

    @Test
    void me_conTokenFirmadoConClaveDistinta_devuelve401() {
        // Simula un token estructuralmente valido (mismo formato) pero firmado con
        // una clave que el servidor no reconoce: JwtService.validarYObtenerClaims
        // debe rechazarlo por firma invalida.
        SecretKey otraClave = Keys.hmacShaKeyFor(
                "otra-clave-secreta-completamente-distinta-a-la-del-servidor-de-pruebas".getBytes(StandardCharsets.UTF_8));
        String tokenForjado = Jwts.builder()
                .subject(AuthTestHelper.ADMIN_USERNAME)
                .claim("uid", 1L)
                .claim("rol", "ADMIN")
                .claim("permisos", Set.of("USUARIO_LEER"))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(otraClave)
                .compact();

        given()
                .header("Authorization", "Bearer " + tokenForjado)
                .when().get("/auth/me")
                .then().statusCode(401);
    }

    @Test
    void me_conAccessTokenTrasHacerLogout_siguePermitiendoAccesoHastaQueExpire_knownBehavior() {
        // Regla inferida importante: el logout SOLO revoca el refresh token. El
        // access token es un JWT stateless (no se consulta base de datos en cada
        // request, ver JwtAuthenticationFilter), por lo que sigue siendo valido
        // hasta su expiracion natural (15 min por defecto) aunque el usuario haya
        // cerrado sesion. No es un bug de este endpoint en particular, sino una
        // caracteristica inherente al diseño JWT stateless del sistema.
        TokenResponse sesion = AuthTestHelper.loginComoAdmin();

        AuthTestHelper.autenticado(sesion.accessToken())
                .body(new RefreshTokenRequest(sesion.refreshToken()))
                .when().post("/auth/logout")
                .then().statusCode(204);

        AuthTestHelper.autenticado(sesion.accessToken())
                .when().get("/auth/me")
                .then().statusCode(200);
    }
}
