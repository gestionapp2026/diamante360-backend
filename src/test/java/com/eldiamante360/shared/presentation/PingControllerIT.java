package com.eldiamante360.shared.presentation;

import com.eldiamante360.shared.it.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Pruebas de integracion de {@link PingController} (GET /ping).
 *
 * <p>Debe responder sin autenticacion: es el endpoint que un monitor
 * externo (UptimeRobot) consulta periodicamente para evitar que Render
 * duerma el servicio por inactividad.
 */
class PingControllerIT extends BaseIntegrationTest {

    @Test
    void ping_sinAutenticacion_devuelve200ConStatusOk() {
        given().when().get("/ping")
                .then().statusCode(200)
                .body("status", equalTo("ok"))
                .body("timestamp", notNullValue());
    }
}
