package com.eldiamante360.insumoquimico.presentation.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarSalidaInsumoRequestValidationTest {

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
        RegistrarSalidaInsumoRequest request = new RegistrarSalidaInsumoRequest(100L, BigDecimal.valueOf(4),
                "Uso en produccion");

        Set<ConstraintViolation<RegistrarSalidaInsumoRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void loteIdNulo_produceViolacion() {
        RegistrarSalidaInsumoRequest request = new RegistrarSalidaInsumoRequest(null, BigDecimal.valueOf(4),
                "Uso en produccion");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void cantidadCeroOMenor_produceViolacion() {
        RegistrarSalidaInsumoRequest request = new RegistrarSalidaInsumoRequest(100L, BigDecimal.ZERO,
                "Uso en produccion");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void motivoVacio_produceViolacion() {
        RegistrarSalidaInsumoRequest request = new RegistrarSalidaInsumoRequest(100L, BigDecimal.valueOf(4), "");

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
