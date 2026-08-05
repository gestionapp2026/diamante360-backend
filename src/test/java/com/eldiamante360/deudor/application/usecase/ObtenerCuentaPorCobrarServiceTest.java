package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerCuentaPorCobrarServiceTest {

    @Mock
    private CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    private ObtenerCuentaPorCobrarService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerCuentaPorCobrarService(cuentaPorCobrarRepositoryPort);
    }

    private CuentaPorCobrar cuentaPendiente(Long id) {
        return new CuentaPorCobrar(id, 100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), EstadoCuentaPorCobrar.PENDIENTE, 9L, null, null,
                null, 0);
    }

    @Test
    void ejecutar_conCuentaExistente_retornaLaCuenta() {
        when(cuentaPorCobrarRepositoryPort.buscarPorId(50L)).thenReturn(Optional.of(cuentaPendiente(50L)));

        CuentaPorCobrarResult resultado = service.ejecutar(50L);

        assertThat(resultado.id()).isEqualTo(50L);
        assertThat(resultado.numeroFactura()).isEqualTo("FAC-2026-00001");
    }

    @Test
    void ejecutar_conCuentaInexistente_lanzaExcepcion() {
        when(cuentaPorCobrarRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
