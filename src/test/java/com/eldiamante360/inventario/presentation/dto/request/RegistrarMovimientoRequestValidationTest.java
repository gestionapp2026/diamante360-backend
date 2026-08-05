package com.eldiamante360.inventario.presentation.dto.request;

import com.eldiamante360.inventario.domain.model.TipoMovimiento;
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

class RegistrarMovimientoRequestValidationTest {

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
        RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA,
                BigDecimal.valueOf(5), "Compra a proveedor");

        Set<ConstraintViolation<RegistrarMovimientoRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void tipoMovimientoNulo_produceViolacion() {
        RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(null, BigDecimal.valueOf(5), "Compra");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void cantidadNegativa_produceViolacion() {
        RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA,
                BigDecimal.valueOf(-1), "Compra");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void motivoVacio_produceViolacion() {
        RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(TipoMovimiento.ENTRADA,
                BigDecimal.valueOf(5), "");

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
