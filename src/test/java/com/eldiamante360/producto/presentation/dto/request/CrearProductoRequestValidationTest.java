package com.eldiamante360.producto.presentation.dto.request;

import com.eldiamante360.producto.domain.model.TipoVenta;
import com.eldiamante360.producto.domain.model.UnidadMedida;
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

class CrearProductoRequestValidationTest {

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
        CrearProductoRequest request = new CrearProductoRequest("Chorizo", 1L, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        Set<ConstraintViolation<CrearProductoRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void nombreVacio_produceViolacion() {
        CrearProductoRequest request = new CrearProductoRequest("", 1L, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void categoriaIdNula_produceViolacion() {
        CrearProductoRequest request = new CrearProductoRequest("Chorizo", null, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void precioCompraNegativo_produceViolacion() {
        CrearProductoRequest request = new CrearProductoRequest("Chorizo", 1L, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(-1), BigDecimal.valueOf(1500), BigDecimal.TEN, BigDecimal.ONE);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void stockInicialNegativo_produceViolacion() {
        CrearProductoRequest request = new CrearProductoRequest("Chorizo", 1L, TipoVenta.UNIDAD, UnidadMedida.UND,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.valueOf(-1), BigDecimal.ONE);

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
