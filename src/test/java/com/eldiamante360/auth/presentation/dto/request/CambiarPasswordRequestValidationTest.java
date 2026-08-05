package com.eldiamante360.auth.presentation.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarPasswordRequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void requestValido_noProduceViolaciones() {
        CambiarPasswordRequest request = new CambiarPasswordRequest("actual123", "nuevaClave123");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void passwordActualVacia_produceViolacion() {
        CambiarPasswordRequest request = new CambiarPasswordRequest("", "nuevaClave123");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"soloLetras", "12345678", "corta1"})
    void passwordNueva_sinLetraYNumero_produceViolacion(String passwordNueva) {
        CambiarPasswordRequest request = new CambiarPasswordRequest("actual123", passwordNueva);

        Set<ConstraintViolation<CambiarPasswordRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isNotEmpty();
    }
}
