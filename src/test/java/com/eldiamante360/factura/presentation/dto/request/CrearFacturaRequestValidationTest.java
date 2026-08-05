package com.eldiamante360.factura.presentation.dto.request;

import com.eldiamante360.factura.domain.model.TipoPago;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CrearFacturaRequestValidationTest {

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

    private DetalleFacturaRequest detalleValido() {
        return new DetalleFacturaRequest(5L, BigDecimal.valueOf(2), BigDecimal.ZERO);
    }

    @Test
    void requestValido_noProduceViolaciones() {
        CrearFacturaRequest request = new CrearFacturaRequest(1L, TipoPago.CONTADO, List.of(detalleValido()), null);

        Set<ConstraintViolation<CrearFacturaRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void clienteIdNulo_produceViolacion() {
        CrearFacturaRequest request = new CrearFacturaRequest(null, TipoPago.CONTADO, List.of(detalleValido()), null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void tipoPagoNulo_produceViolacion() {
        CrearFacturaRequest request = new CrearFacturaRequest(1L, null, List.of(detalleValido()), null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void detallesNulos_produceViolacion() {
        CrearFacturaRequest request = new CrearFacturaRequest(1L, TipoPago.CONTADO, null, null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void detallesVacios_produceViolacion() {
        CrearFacturaRequest request = new CrearFacturaRequest(1L, TipoPago.CONTADO, List.of(), null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void detalleInvalidoDentroDeLaLista_propagaLaViolacionPorCascada() {
        DetalleFacturaRequest detalleInvalido = new DetalleFacturaRequest(5L, BigDecimal.ZERO, BigDecimal.ZERO);
        CrearFacturaRequest request = new CrearFacturaRequest(1L, TipoPago.CONTADO, List.of(detalleInvalido), null);

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
