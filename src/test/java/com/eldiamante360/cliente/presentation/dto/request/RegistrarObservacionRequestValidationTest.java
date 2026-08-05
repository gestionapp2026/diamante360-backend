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

class RegistrarObservacionRequestValidationTest {

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
        RegistrarObservacionRequest request = new RegistrarObservacionRequest("Cliente prefiere entregas en la manana");

        Set<ConstraintViolation<RegistrarObservacionRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void textoVacio_produceViolacion() {
        RegistrarObservacionRequest request = new RegistrarObservacionRequest("");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void textoExcedeLongitud_produceViolacion() {
        RegistrarObservacionRequest request = new RegistrarObservacionRequest("a".repeat(501));

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
