package com.eldiamante360.shared.it;

import com.eldiamante360.shared.presentation.ApiErrorResponse;
import io.restassured.response.ValidatableResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Aserciones comunes sobre el cuerpo de error estandar de la API
 * ({@link ApiErrorResponse}), para no repetir esta logica en cada clase
 * *IT. Ver GlobalExceptionHandler para el contrato exacto.
 */
public final class ApiErrorAssertions {

    private ApiErrorAssertions() {
    }

    /** Verifica la forma general de un ApiErrorResponse (status, error, message y path no vacios/coherentes). */
    public static ApiErrorResponse verificarFormaEstandar(ValidatableResponse respuesta, int statusEsperado) {
        ApiErrorResponse error = respuesta.extract().as(ApiErrorResponse.class);
        assertThat(error.timestamp()).isNotNull();
        assertThat(error.status()).isEqualTo(statusEsperado);
        assertThat(error.error()).isNotBlank();
        assertThat(error.message()).isNotBlank();
        assertThat(error.path()).isNotBlank();
        return error;
    }

    /** Verifica que exista un error de validacion (400) para un campo especifico, con el mensaje esperado. */
    public static void verificarErrorDeCampo(ApiErrorResponse error, String campo, String mensajeEsperado) {
        assertThat(error.errores())
                .anySatisfy(campoError -> {
                    assertThat(campoError.campo()).isEqualTo(campo);
                    if (mensajeEsperado != null) {
                        assertThat(campoError.mensaje()).isEqualTo(mensajeEsperado);
                    }
                });
    }

    /** Verifica que exista un error de validacion (400) para un campo especifico, sin importar el mensaje exacto. */
    public static void verificarErrorDeCampo(ApiErrorResponse error, String campo) {
        verificarErrorDeCampo(error, campo, null);
    }
}
