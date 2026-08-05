package com.eldiamante360.shared.it;

import com.eldiamante360.auth.presentation.dto.request.CrearUsuarioRequest;
import com.eldiamante360.auth.presentation.dto.request.LoginRequest;
import com.eldiamante360.auth.presentation.dto.response.RolResponse;
import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Utilidades de autenticacion para las pruebas de integracion.
 *
 * <p>Todo aqui pasa por peticiones HTTP reales (login real via
 * {@code POST /auth/login}, creacion real de usuarios via
 * {@code POST /usuarios}) para ejercitar el pipeline completo de
 * autenticacion/autorizacion tal como lo haria un cliente real, en vez de
 * fabricar tokens o entidades a mano.
 */
public final class AuthTestHelper {

    /** Administradores sembrados por AdminUserSeeder en el primer arranque de la aplicacion. */
    public static final String ADMIN_USERNAME = "jhon";
    public static final String ADMIN_USERNAME_ALTERNO = "angie";
    public static final String ADMIN_PASSWORD = BaseIntegrationTest.ADMIN_DEFAULT_PASSWORD_TEST;

    private AuthTestHelper() {
    }

    public static Response loginRaw(String username, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(new LoginRequest(username, password))
                .when()
                .post("/auth/login");
    }

    public static TokenResponse login(String username, String password) {
        return loginRaw(username, password)
                .then().statusCode(200)
                .extract().as(TokenResponse.class);
    }

    public static TokenResponse loginComoAdmin() {
        return login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    /** RequestSpecification con el header Authorization ya puesto, lista para encadenar .when().get(...), etc. */
    public static RequestSpecification autenticado(String accessToken) {
        return given().contentType(ContentType.JSON).header("Authorization", "Bearer " + accessToken);
    }

    public static RequestSpecification autenticadoComoAdmin() {
        return autenticado(loginComoAdmin().accessToken());
    }

    /**
     * Crea (via API real, autenticado como ADMIN) un usuario de prueba con el
     * rol indicado y hace login con el, devolviendo su sesion. Util para
     * pruebas de autorizacion negativa (rol/permiso insuficiente) sin
     * depender de datos sembrados fijos.
     */
    public static TokenResponse crearUsuarioYLogin(String rolNombre) {
        Long rolId = obtenerRolIdPorNombre(rolNombre);
        String username = TestDataFactory.username("it_" + rolNombre.toLowerCase());
        String password = TestDataFactory.passwordValida();

        var request = new CrearUsuarioRequest(username, password, TestDataFactory.nombreCompleto("IT " + rolNombre), rolId);
        autenticadoComoAdmin()
                .body(request)
                .when().post("/usuarios")
                .then().statusCode(201);

        return login(username, password);
    }

    public static Long obtenerRolIdPorNombre(String nombre) {
        List<RolResponse> roles = List.of(
                autenticadoComoAdmin()
                        .when().get("/roles")
                        .then().statusCode(200)
                        .extract().as(RolResponse[].class));
        return roles.stream()
                .filter(r -> r.nombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Rol de prueba no encontrado: " + nombre))
                .id();
    }
}
