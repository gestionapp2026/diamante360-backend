package com.eldiamante360.cliente.presentation.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarEstadoRequestValidationTest {

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
        CambiarEstadoRequest request = new CambiarEstadoRequest(true);

        Set<ConstraintViolation<CambiarEstadoRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void activoNulo_produceViolacion() {
        CambiarEstadoRequest request = new CambiarEstadoRequest(null);

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
