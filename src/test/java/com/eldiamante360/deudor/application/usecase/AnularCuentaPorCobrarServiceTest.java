package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.application.port.HistorialCuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.exception.CuentaPorCobrarAnuladaException;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.HistorialCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.TipoEventoCuentaPorCobrar;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnularCuentaPorCobrarServiceTest {

    @Mock
    private CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    @Mock
    private HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort;

    private AnularCuentaPorCobrarService service;

    @BeforeEach
    void setUp() {
        service = new AnularCuentaPorCobrarService(cuentaPorCobrarRepositoryPort, historialCuentaPorCobrarRepositoryPort);
    }

    private CuentaPorCobrar cuentaPendiente(Long id, Long facturaId, String numeroFactura) {
        return new CuentaPorCobrar(id, facturaId, numeroFactura, 1L, "Juan Perez", "123456789",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), EstadoCuentaPorCobrar.PENDIENTE, 9L, null, null,
                null, 0);
    }

    @Test
    void ejecutar_conCuentaPendiente_laAnulaYRegistraHistorialDeAnulacion() {
        CuentaPorCobrar cuenta = cuentaPendiente(50L, 100L, "FAC-2026-00001");
        when(cuentaPorCobrarRepositoryPort.buscarPorFacturaId(100L)).thenReturn(Optional.of(cuenta));
        when(cuentaPorCobrarRepositoryPort.guardar(any(CuentaPorCobrar.class))).thenAnswer(inv -> inv.getArgument(0));
        when(historialCuentaPorCobrarRepositoryPort.guardar(any(HistorialCuentaPorCobrar.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CuentaPorCobrarResult resultado = service.ejecutar(100L, 9L);

        assertThat(resultado.estado()).isEqualTo(EstadoCuentaPorCobrar.ANULADA);
        assertThat(resultado.fechaAnulacion()).isNotNull();

        ArgumentCaptor<CuentaPorCobrar> cuentaCaptor = ArgumentCaptor.forClass(CuentaPorCobrar.class);
        verify(cuentaPorCobrarRepositoryPort).guardar(cuentaCaptor.capture());
        assertThat(cuentaCaptor.getValue().getEstado()).isEqualTo(EstadoCuentaPorCobrar.ANULADA);

        ArgumentCaptor<HistorialCuentaPorCobrar> historialCaptor = ArgumentCaptor.forClass(HistorialCuentaPorCobrar.class);
        verify(historialCuentaPorCobrarRepositoryPort).guardar(historialCaptor.capture());
        HistorialCuentaPorCobrar historial = historialCaptor.getValue();
        assertThat(historial.cuentaPorCobrarId()).isEqualTo(50L);
        assertThat(historial.tipoEvento()).isEqualTo(TipoEventoCuentaPorCobrar.ANULACION);
        assertThat(historial.descripcion()).isEqualTo("Cuenta por cobrar anulada por anulacion de la factura FAC-2026-00001");
        assertThat(historial.usuarioId()).isEqualTo(9L);
    }

    @Test
    void ejecutar_conCuentaInexistenteParaLaFactura_lanzaExcepcionYNoInteractuaConElHistorial() {
        when(cuentaPorCobrarRepositoryPort.buscarPorFacturaId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, 9L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verifyNoInteractions(historialCuentaPorCobrarRepositoryPort);
    }

    @Test
    void ejecutar_conCuentaYaAnulada_lanzaExcepcionYNoGuardaNadaMas() {
        CuentaPorCobrar cuenta = cuentaPendiente(50L, 100L, "FAC-2026-00001");
        cuenta.anular();
        when(cuentaPorCobrarRepositoryPort.buscarPorFacturaId(100L)).thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> service.ejecutar(100L, 9L))
                .isInstanceOf(CuentaPorCobrarAnuladaException.class);

        verifyNoInteractions(historialCuentaPorCobrarRepositoryPort);
    }
}
