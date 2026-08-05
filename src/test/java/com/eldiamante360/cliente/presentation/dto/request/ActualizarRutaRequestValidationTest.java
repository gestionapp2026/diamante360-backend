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

class ActualizarRutaRequestValidationTest {

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
        ActualizarRutaRequest request = new ActualizarRutaRequest("Ruta Norte Extendida", "Zona norte y noroccidente");

        Set<ConstraintViolation<ActualizarRutaRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void nombreVacio_produceViolacion() {
        ActualizarRutaRequest request = new ActualizarRutaRequest("", "Zona norte");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void nombreExcedeLongitud_produceViolacion() {
        ActualizarRutaRequest request = new ActualizarRutaRequest("a".repeat(81), "Zona norte");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void descripcionExcedeLongitud_produceViolacion() {
        ActualizarRutaRequest request = new ActualizarRutaRequest("Ruta Norte", "a".repeat(201));

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void descripcionNula_noProduceViolacion() {
        ActualizarRutaRequest request = new ActualizarRutaRequest("Ruta Norte", null);

        assertThat(validator.validate(request)).isEmpty();
    }
}
