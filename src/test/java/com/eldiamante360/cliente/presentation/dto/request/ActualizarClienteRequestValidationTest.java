package com.eldiamante360.cliente.presentation.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ActualizarClienteRequestValidationTest {

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
        ActualizarClienteRequest request = new ActualizarClienteRequest("Juan Perez", List.of("3001234567"),
                "juan@correo.com", "Calle 1 # 2-3");

        Set<ConstraintViolation<ActualizarClienteRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void nombreVacio_produceViolacion() {
        ActualizarClienteRequest request = new ActualizarClienteRequest("", List.of("3001234567"),
                "juan@correo.com", "Calle 1 # 2-3");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void nombreExcedeLongitud_produceViolacion() {
        ActualizarClienteRequest request = new ActualizarClienteRequest("a".repeat(151), List.of("3001234567"),
                "juan@correo.com", "Calle 1 # 2-3");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void telefonoExcedeLongitud_produceViolacion() {
        ActualizarClienteRequest request = new ActualizarClienteRequest("Juan Perez", List.of("1".repeat(21)),
                "juan@correo.com", "Calle 1 # 2-3");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void emailConFormatoInvalido_produceViolacion() {
        ActualizarClienteRequest request = new ActualizarClienteRequest("Juan Perez", List.of("3001234567"),
                "correo-invalido", "Calle 1 # 2-3");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void direccionExcedeLongitud_produceViolacion() {
        ActualizarClienteRequest request = new ActualizarClienteRequest("Juan Perez", List.of("3001234567"),
                "juan@correo.com", "a".repeat(201));

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void telefonoEmailDireccionNulos_noProduceViolacion() {
        ActualizarClienteRequest request = new ActualizarClienteRequest("Juan Perez", null, null, null);

        assertThat(validator.validate(request)).isEmpty();
    }
}
