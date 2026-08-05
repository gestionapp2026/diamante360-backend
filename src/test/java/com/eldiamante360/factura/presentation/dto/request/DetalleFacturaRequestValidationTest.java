package com.eldiamante360.factura.presentation.dto.request;

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

class DetalleFacturaRequestValidationTest {

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
        DetalleFacturaRequest request = new DetalleFacturaRequest(5L, BigDecimal.valueOf(2), BigDecimal.TEN);

        Set<ConstraintViolation<DetalleFacturaRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void productoIdNulo_produceViolacion() {
        DetalleFacturaRequest request = new DetalleFacturaRequest(null, BigDecimal.valueOf(2), BigDecimal.TEN);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void cantidadNula_produceViolacion() {
        DetalleFacturaRequest request = new DetalleFacturaRequest(5L, null, BigDecimal.TEN);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void cantidadCero_produceViolacion() {
        DetalleFacturaRequest request = new DetalleFacturaRequest(5L, BigDecimal.ZERO, BigDecimal.TEN);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void cantidadNegativa_produceViolacion() {
        DetalleFacturaRequest request = new DetalleFacturaRequest(5L, BigDecimal.valueOf(-1), BigDecimal.TEN);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void porcentajeDescuentoNulo_produceViolacion() {
        DetalleFacturaRequest request = new DetalleFacturaRequest(5L, BigDecimal.valueOf(2), null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void porcentajeDescuentoNegativo_produceViolacion() {
        DetalleFacturaRequest request = new DetalleFacturaRequest(5L, BigDecimal.valueOf(2), BigDecimal.valueOf(-1));

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void porcentajeDescuentoMayorACien_produceViolacion() {
        DetalleFacturaRequest request = new DetalleFacturaRequest(5L, BigDecimal.valueOf(2), BigDecimal.valueOf(101));

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void porcentajeDescuentoEnLosLimites_noProduceViolacion() {
        DetalleFacturaRequest requestCero = new DetalleFacturaRequest(5L, BigDecimal.valueOf(2), BigDecimal.ZERO);
        DetalleFacturaRequest requestCien = new DetalleFacturaRequest(5L, BigDecimal.valueOf(2), BigDecimal.valueOf(100));

        assertThat(validator.validate(requestCero)).isEmpty();
        assertThat(validator.validate(requestCien)).isEmpty();
    }
}
