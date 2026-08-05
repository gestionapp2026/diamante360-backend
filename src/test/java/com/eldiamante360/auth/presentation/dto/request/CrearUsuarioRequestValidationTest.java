package com.eldiamante360.auth.presentation.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CrearUsuarioRequestValidationTest {

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
        CrearUsuarioRequest request = new CrearUsuarioRequest("jhon.perez", "Secreta123", "Jhon Perez", 1L);

        Set<ConstraintViolation<CrearUsuarioRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"jh", "usuario con espacios", "usuario#invalido", ""})
    void username_invalido_produceViolacion(String username) {
        CrearUsuarioRequest request = new CrearUsuarioRequest(username, "Secreta123", "Jhon Perez", 1L);

        Set<ConstraintViolation<CrearUsuarioRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isNotEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"soloLetras", "12345678", "corta1", ""})
    void password_invalida_produceViolacion(String password) {
        CrearUsuarioRequest request = new CrearUsuarioRequest("jhon", password, "Jhon Perez", 1L);

        Set<ConstraintViolation<CrearUsuarioRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isNotEmpty();
    }

    @Test
    void rolId_nulo_produceViolacion() {
        CrearUsuarioRequest request = new CrearUsuarioRequest("jhon", "Secreta123", "Jhon Perez", null);

        Set<ConstraintViolation<CrearUsuarioRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isNotEmpty();
    }
}
