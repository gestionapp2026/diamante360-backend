package com.eldiamante360.cliente.presentation.dto.request;

import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
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

class CrearClienteRequestValidationTest {

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
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", null);

        Set<ConstraintViolation<CrearClienteRequest>> violaciones = validator.validate(request);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void tipoDocumentoNulo_produceViolacion() {
        CrearClienteRequest request = new CrearClienteRequest(null, "123456789", "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void numeroDocumentoVacio_produceViolacion() {
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "", "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void numeroDocumentoExcedeLongitud_produceViolacion() {
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "1".repeat(21), "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void nombreVacio_produceViolacion() {
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "123456789", "",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void nombreExcedeLongitud_produceViolacion() {
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "123456789", "a".repeat(151),
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void telefonoExcedeLongitud_produceViolacion() {
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("1".repeat(21)), "juan@correo.com", "Calle 1 # 2-3", null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void emailConFormatoInvalido_produceViolacion() {
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("3001234567"), "correo-invalido", "Calle 1 # 2-3", null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void emailExcedeLongitud_produceViolacion() {
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("3001234567"), "a".repeat(115) + "@x.com", "Calle 1 # 2-3", null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void direccionExcedeLongitud_produceViolacion() {
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "a".repeat(201), null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void masDeCincoTelefonos_produceViolacion() {
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("1", "2", "3", "4", "5", "6"), "juan@correo.com", "Calle 1 # 2-3", null);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void telefonoEmailDireccionYRutaIdNulos_noProduceViolacion() {
        CrearClienteRequest request = new CrearClienteRequest(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                null, null, null, null);

        assertThat(validator.validate(request)).isEmpty();
    }
}
