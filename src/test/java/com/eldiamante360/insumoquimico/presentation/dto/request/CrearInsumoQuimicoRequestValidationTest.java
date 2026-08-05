package com.eldiamante360.insumoquimico.presentation.dto.request;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CrearInsumoQuimicoRequestValidationTest {

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
        CrearInsumoQuimicoRequest request = new CrearInsumoQuimicoRequest("Sal de cura", UnidadMedidaInsumo.KG);

        Set<ConstraintViolation<CrearInsumoQuimicoRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void nombreVacio_produceViolacion() {
        CrearInsumoQuimicoRequest request = new CrearInsumoQuimicoRequest("", UnidadMedidaInsumo.KG);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void nombreDemasiadoLargo_produceViolacion() {
        String nombreLargo = "A".repeat(121);
        CrearInsumoQuimicoRequest request = new CrearInsumoQuimicoRequest(nombreLargo, UnidadMedidaInsumo.KG);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void unidadMedidaNula_produceViolacion() {
        CrearInsumoQuimicoRequest request = new CrearInsumoQuimicoRequest("Sal de cura", null);

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
