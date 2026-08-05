package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.HistorialCuentaPorCobrarResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.application.port.HistorialCuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.HistorialCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.TipoEventoCuentaPorCobrar;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarHistorialCuentaPorCobrarServiceTest {

    @Mock
    private HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort;

    @Mock
    private CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    private ListarHistorialCuentaPorCobrarService service;

    @BeforeEach
    void setUp() {
        service = new ListarHistorialCuentaPorCobrarService(historialCuentaPorCobrarRepositoryPort,
                cuentaPorCobrarRepositoryPort);
    }

    private CuentaPorCobrar cuentaPendiente(Long id) {
        return new CuentaPorCobrar(id, 100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), EstadoCuentaPorCobrar.PENDIENTE, 9L, null, null,
                null, 0);
    }

    @Test
    void ejecutar_conCuentaExistente_retornaLaPaginaMapeada() {
        HistorialCuentaPorCobrar historial = HistorialCuentaPorCobrar.nuevo(50L, TipoEventoCuentaPorCobrar.CREACION,
                "Credito registrado por factura FAC-2026-00001 por valor de 1000", 9L);
        Pageable pageable = PageRequest.of(0, 10);
        when(cuentaPorCobrarRepositoryPort.buscarPorId(50L)).thenReturn(Optional.of(cuentaPendiente(50L)));
        when(historialCuentaPorCobrarRepositoryPort.listarPorCuenta(50L, pageable))
                .thenReturn(new PageImpl<>(List.of(historial)));

        Page<HistorialCuentaPorCobrarResult> resultado = service.ejecutar(50L, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).tipoEvento()).isEqualTo(TipoEventoCuentaPorCobrar.CREACION);
    }

    @Test
    void ejecutar_conCuentaInexistente_lanzaExcepcionYNoInteractuaConElHistorial() {
        Pageable pageable = PageRequest.of(0, 10);
        when(cuentaPorCobrarRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, pageable))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verifyNoInteractions(historialCuentaPorCobrarRepositoryPort);
    }
}
