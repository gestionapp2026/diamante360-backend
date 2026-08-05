package com.eldiamante360.producto.presentation.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CrearCategoriaRequestValidationTest {

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
        CrearCategoriaRequest request = new CrearCategoriaRequest("Ahumados", "Productos ahumados");

        Set<ConstraintViolation<CrearCategoriaRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void nombreVacio_produceViolacion() {
        CrearCategoriaRequest request = new CrearCategoriaRequest("", "Productos ahumados");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void nombreExcedeLongitud_produceViolacion() {
        CrearCategoriaRequest request = new CrearCategoriaRequest("a".repeat(61), "Productos ahumados");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void descripcionNula_noProduceViolacion() {
        CrearCategoriaRequest request = new CrearCategoriaRequest("Ahumados", null);

        assertThat(validator.validate(request)).isEmpty();
    }
}
