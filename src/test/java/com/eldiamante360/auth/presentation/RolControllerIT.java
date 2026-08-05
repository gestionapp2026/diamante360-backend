package com.eldiamante360.auth.presentation;

import com.eldiamante360.auth.presentation.dto.response.RolResponse;
import com.eldiamante360.auth.presentation.dto.response.TokenResponse;
import com.eldiamante360.shared.it.AuthTestHelper;
import com.eldiamante360.shared.it.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integracion de {@link RolController} (GET /roles).
 */
class RolControllerIT extends BaseIntegrationTest {

    @Test
    void listar_comoAdmin_devuelveLosDosRolesSembradosConSusPermisos() {
        TokenResponse admin = AuthTestHelper.loginComoAdmin();

        List<RolResponse> roles = List.of(
                AuthTestHelper.autenticado(admin.accessToken())
                        .when().get("/roles")
                        .then().statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract().as(RolResponse[].class));

        assertThat(roles).extracting(RolResponse::nombre).contains("ADMIN", "VENDEDOR");

        RolResponse rolAdmin = roles.stream().filter(r -> r.nombre().equals("ADMIN")).findFirst().orElseThrow();
        // ADMIN recibe TODOS los permisos existentes (ver V1__create_seguridad.sql, CROSS JOIN).
        assertThat(rolAdmin.permisos()).contains(
                "USUARIO_CREAR", "USUARIO_LEER", "USUARIO_EDITAR", "USUARIO_ELIMINAR",
                "ROL_GESTIONAR", "INVENTARIO_AJUSTAR", "INSUMO_AJUSTAR");

        RolResponse rolVendedor = roles.stream().filter(r -> r.nombre().equals("VENDEDOR")).findFirst().orElseThrow();
        // VENDEDOR: factura, consulta cartera de deudores y ciclo completo de ordenes (ver V14__add_permisos_orden.sql).
        assertThat(rolVendedor.permisos()).containsExactlyInAnyOrder(
                "DASHBOARD_LEER", "PRODUCTO_LEER", "CLIENTE_LEER",
                "FACTURA_LEER", "FACTURA_CREAR", "DEUDOR_LEER", "DEUDOR_ABONAR",
                "ORDEN_LEER", "ORDEN_CREAR", "ORDEN_DESPACHAR", "ORDEN_ANULAR");
    }

    @Test
    void listar_sinAutenticacion_devuelve401() {
        given().when().get("/roles").then().statusCode(401);
    }

    @Test
    void listar_comoVendedorSinNingunoDeLosPermisosRequeridos_devuelve403() {
        // VENDEDOR no tiene ROL_GESTIONAR, USUARIO_CREAR ni USUARIO_EDITAR (los 3 autorizados en @PreAuthorize).
        TokenResponse vendedor = AuthTestHelper.crearUsuarioYLogin("VENDEDOR");

        AuthTestHelper.autenticado(vendedor.accessToken())
                .when().get("/roles")
                .then().statusCode(403);
    }
}
