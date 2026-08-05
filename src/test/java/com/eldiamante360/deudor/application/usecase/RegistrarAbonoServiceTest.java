package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.dto.RegistrarAbonoCommand;
import com.eldiamante360.deudor.application.port.AbonoRepositoryPort;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.application.port.HistorialCuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.Abono;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.HistorialCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.TipoEventoCuentaPorCobrar;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.domain.model.MedioPago;
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
class RegistrarAbonoServiceTest {

    @Mock
    private CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    @Mock
    private AbonoRepositoryPort abonoRepositoryPort;

    @Mock
    private HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort;

    private RegistrarAbonoService service;

    @BeforeEach
    void setUp() {
        service = new RegistrarAbonoService(cuentaPorCobrarRepositoryPort, abonoRepositoryPort,
                historialCuentaPorCobrarRepositoryPort);
    }

    private CuentaPorCobrar cuentaPendiente(Long id, BigDecimal montoOriginal) {
        return new CuentaPorCobrar(id, 100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789", montoOriginal,
                montoOriginal, EstadoCuentaPorCobrar.PENDIENTE, 9L, null, null, null, 0);
    }

    @Test
    void ejecutar_conAbonoParcial_reduceElSaldoYRegistraHistorialDeAbono() {
        CuentaPorCobrar cuenta = cuentaPendiente(50L, BigDecimal.valueOf(1000));
        RegistrarAbonoCommand command = new RegistrarAbonoCommand(50L, BigDecimal.valueOf(400), 9L, MedioPago.NEQUI);
        when(cuentaPorCobrarRepositoryPort.buscarPorId(50L)).thenReturn(Optional.of(cuenta));
        when(cuentaPorCobrarRepositoryPort.guardar(any(CuentaPorCobrar.class))).thenAnswer(inv -> inv.getArgument(0));
        when(abonoRepositoryPort.guardar(any(Abono.class))).thenAnswer(inv -> inv.getArgument(0));
        when(historialCuentaPorCobrarRepositoryPort.guardar(any(HistorialCuentaPorCobrar.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CuentaPorCobrarResult resultado = service.ejecutar(command);

        assertThat(resultado.estado()).isEqualTo(EstadoCuentaPorCobrar.PARCIAL);
        assertThat(resultado.saldoPendiente()).isEqualByComparingTo(BigDecimal.valueOf(600));

        ArgumentCaptor<CuentaPorCobrar> cuentaCaptor = ArgumentCaptor.forClass(CuentaPorCobrar.class);
        verify(cuentaPorCobrarRepositoryPort).guardar(cuentaCaptor.capture());
        assertThat(cuentaCaptor.getValue().getSaldoPendiente()).isEqualByComparingTo(BigDecimal.valueOf(600));
        assertThat(cuentaCaptor.getValue().getEstado()).isEqualTo(EstadoCuentaPorCobrar.PARCIAL);

        ArgumentCaptor<Abono> abonoCaptor = ArgumentCaptor.forClass(Abono.class);
        verify(abonoRepositoryPort).guardar(abonoCaptor.capture());
        Abono abono = abonoCaptor.getValue();
        assertThat(abono.cuentaPorCobrarId()).isEqualTo(50L);
        assertThat(abono.monto()).isEqualByComparingTo(BigDecimal.valueOf(400));
        assertThat(abono.usuarioId()).isEqualTo(9L);
        assertThat(abono.medioPago()).isEqualTo(MedioPago.NEQUI);

        ArgumentCaptor<HistorialCuentaPorCobrar> historialCaptor = ArgumentCaptor.forClass(HistorialCuentaPorCobrar.class);
        verify(historialCuentaPorCobrarRepositoryPort).guardar(historialCaptor.capture());
        HistorialCuentaPorCobrar historial = historialCaptor.getValue();
        assertThat(historial.cuentaPorCobrarId()).isEqualTo(50L);
        assertThat(historial.tipoEvento()).isEqualTo(TipoEventoCuentaPorCobrar.ABONO);
        assertThat(historial.descripcion()).isEqualTo("Abono de 400 registrado, saldo pendiente 600");
        assertThat(historial.usuarioId()).isEqualTo(9L);
    }

    @Test
    void ejecutar_conAbonoQueCubreElSaldoTotal_dejaLaCuentaPagadaYRegistraHistorialDePagoTotal() {
        CuentaPorCobrar cuenta = cuentaPendiente(50L, BigDecimal.valueOf(1000));
        RegistrarAbonoCommand command = new RegistrarAbonoCommand(50L, BigDecimal.valueOf(1000), 9L, MedioPago.EFECTIVO);
        when(cuentaPorCobrarRepositoryPort.buscarPorId(50L)).thenReturn(Optional.of(cuenta));
        when(cuentaPorCobrarRepositoryPort.guardar(any(CuentaPorCobrar.class))).thenAnswer(inv -> inv.getArgument(0));
        when(abonoRepositoryPort.guardar(any(Abono.class))).thenAnswer(inv -> inv.getArgument(0));
        when(historialCuentaPorCobrarRepositoryPort.guardar(any(HistorialCuentaPorCobrar.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CuentaPorCobrarResult resultado = service.ejecutar(command);

        assertThat(resultado.estado()).isEqualTo(EstadoCuentaPorCobrar.PAGADA);
        assertThat(resultado.saldoPendiente()).isEqualByComparingTo(BigDecimal.ZERO);

        ArgumentCaptor<HistorialCuentaPorCobrar> historialCaptor = ArgumentCaptor.forClass(HistorialCuentaPorCobrar.class);
        verify(historialCuentaPorCobrarRepositoryPort).guardar(historialCaptor.capture());
        HistorialCuentaPorCobrar historial = historialCaptor.getValue();
        assertThat(historial.tipoEvento()).isEqualTo(TipoEventoCuentaPorCobrar.PAGO_TOTAL);
        assertThat(historial.descripcion()).isEqualTo("Abono de 1000 registrado, cuenta pagada en su totalidad");
    }

    @Test
    void ejecutar_conCuentaInexistente_lanzaExcepcionYNoInteractuaConLosDemasPuertos() {
        RegistrarAbonoCommand command = new RegistrarAbonoCommand(404L, BigDecimal.valueOf(100), 9L, MedioPago.EFECTIVO);
        when(cuentaPorCobrarRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verifyNoInteractions(abonoRepositoryPort, historialCuentaPorCobrarRepositoryPort);
    }
}
