package com.eldiamante360.insumoquimico.presentation.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarEntradaInsumoRequestValidationTest {

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
        RegistrarEntradaInsumoRequest request = new RegistrarEntradaInsumoRequest("L-001",
                LocalDate.now().plusDays(30), BigDecimal.TEN, "Compra inicial");

        Set<ConstraintViolation<RegistrarEntradaInsumoRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void conNumeroLoteYFechaVencimientoNulos_noProduceViolaciones() {
        RegistrarEntradaInsumoRequest request = new RegistrarEntradaInsumoRequest(null, null, BigDecimal.TEN,
                "Compra inicial");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void cantidadNula_produceViolacion() {
        RegistrarEntradaInsumoRequest request = new RegistrarEntradaInsumoRequest("L-001",
                LocalDate.now().plusDays(30), null, "Compra inicial");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void cantidadCeroOMenor_produceViolacion() {
        RegistrarEntradaInsumoRequest request = new RegistrarEntradaInsumoRequest("L-001",
                LocalDate.now().plusDays(30), BigDecimal.ZERO, "Compra inicial");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void motivoVacio_produceViolacion() {
        RegistrarEntradaInsumoRequest request = new RegistrarEntradaInsumoRequest("L-001",
                LocalDate.now().plusDays(30), BigDecimal.TEN, "");

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
