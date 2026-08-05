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
class ObtenerCuentaPorCobrarPorFacturaServiceTest {

    @Mock
    private CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    private ObtenerCuentaPorCobrarPorFacturaService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerCuentaPorCobrarPorFacturaService(cuentaPorCobrarRepositoryPort);
    }

    private CuentaPorCobrar cuentaPendiente(Long id, Long facturaId) {
        return new CuentaPorCobrar(id, facturaId, "FAC-2026-00001", 1L, "Juan Perez", "123456789",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), EstadoCuentaPorCobrar.PENDIENTE, 9L, null, null,
                null, 0);
    }

    @Test
    void ejecutar_conFacturaConCuentaAsociada_retornaLaCuenta() {
        when(cuentaPorCobrarRepositoryPort.buscarPorFacturaId(100L)).thenReturn(Optional.of(cuentaPendiente(50L, 100L)));

        CuentaPorCobrarResult resultado = service.ejecutar(100L);

        assertThat(resultado.id()).isEqualTo(50L);
        assertThat(resultado.facturaId()).isEqualTo(100L);
    }

    @Test
    void ejecutar_conFacturaSinCuentaAsociada_lanzaExcepcion() {
        when(cuentaPorCobrarRepositoryPort.buscarPorFacturaId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
