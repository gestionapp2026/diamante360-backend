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

class CrearRutaRequestValidationTest {

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
        CrearRutaRequest request = new CrearRutaRequest("Ruta Norte", "Zona norte");

        Set<ConstraintViolation<CrearRutaRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void nombreVacio_produceViolacion() {
        CrearRutaRequest request = new CrearRutaRequest("", "Zona norte");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void nombreExcedeLongitud_produceViolacion() {
        CrearRutaRequest request = new CrearRutaRequest("a".repeat(81), "Zona norte");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void descripcionExcedeLongitud_produceViolacion() {
        CrearRutaRequest request = new CrearRutaRequest("Ruta Norte", "a".repeat(201));

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void descripcionNula_noProduceViolacion() {
        CrearRutaRequest request = new CrearRutaRequest("Ruta Norte", null);

        assertThat(validator.validate(request)).isEmpty();
    }
}
