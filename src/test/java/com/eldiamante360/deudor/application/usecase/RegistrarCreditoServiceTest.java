package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.dto.RegistrarCreditoCommand;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.application.port.HistorialCuentaPorCobrarRepositoryPort;
import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.HistorialCuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.TipoEventoCuentaPorCobrar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarCreditoServiceTest {

    @Mock
    private CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    @Mock
    private HistorialCuentaPorCobrarRepositoryPort historialCuentaPorCobrarRepositoryPort;

    private RegistrarCreditoService service;

    @BeforeEach
    void setUp() {
        service = new RegistrarCreditoService(cuentaPorCobrarRepositoryPort, historialCuentaPorCobrarRepositoryPort);
    }

    @Test
    void ejecutar_conComandoValido_creaLaCuentaPendienteYRegistraHistorialDeCreacion() {
        RegistrarCreditoCommand command = new RegistrarCreditoCommand(100L, "FAC-2026-00001", 1L, "Juan Perez",
                "123456789", BigDecimal.valueOf(3000), 9L);
        when(cuentaPorCobrarRepositoryPort.guardar(any(CuentaPorCobrar.class))).thenAnswer(inv -> {
            CuentaPorCobrar c = inv.getArgument(0);
            return new CuentaPorCobrar(50L, c.getFacturaId(), c.getNumeroFactura(), c.getClienteId(),
                    c.getClienteNombre(), c.getClienteNumeroDocumento(), c.getMontoOriginal(), c.getSaldoPendiente(),
                    c.getEstado(), c.getUsuarioId(), c.getFecha(), c.getFechaUltimoAbono(), c.getFechaAnulacion(), 0);
        });
        when(historialCuentaPorCobrarRepositoryPort.guardar(any(HistorialCuentaPorCobrar.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CuentaPorCobrarResult resultado = service.ejecutar(command);

        assertThat(resultado.id()).isEqualTo(50L);
        assertThat(resultado.facturaId()).isEqualTo(100L);
        assertThat(resultado.numeroFactura()).isEqualTo("FAC-2026-00001");
        assertThat(resultado.clienteId()).isEqualTo(1L);
        assertThat(resultado.clienteNombre()).isEqualTo("Juan Perez");
        assertThat(resultado.clienteNumeroDocumento()).isEqualTo("123456789");
        assertThat(resultado.montoOriginal()).isEqualByComparingTo(BigDecimal.valueOf(3000));
        assertThat(resultado.saldoPendiente()).isEqualByComparingTo(BigDecimal.valueOf(3000));
        assertThat(resultado.estado()).isEqualTo(EstadoCuentaPorCobrar.PENDIENTE);

        ArgumentCaptor<CuentaPorCobrar> cuentaCaptor = ArgumentCaptor.forClass(CuentaPorCobrar.class);
        verify(cuentaPorCobrarRepositoryPort).guardar(cuentaCaptor.capture());
        CuentaPorCobrar cuentaGuardada = cuentaCaptor.getValue();
        assertThat(cuentaGuardada.getId()).isNull();
        assertThat(cuentaGuardada.getFacturaId()).isEqualTo(100L);
        assertThat(cuentaGuardada.getEstado()).isEqualTo(EstadoCuentaPorCobrar.PENDIENTE);
        assertThat(cuentaGuardada.getMontoOriginal()).isEqualByComparingTo(BigDecimal.valueOf(3000));

        ArgumentCaptor<HistorialCuentaPorCobrar> historialCaptor = ArgumentCaptor.forClass(HistorialCuentaPorCobrar.class);
        verify(historialCuentaPorCobrarRepositoryPort).guardar(historialCaptor.capture());
        HistorialCuentaPorCobrar historial = historialCaptor.getValue();
        assertThat(historial.cuentaPorCobrarId()).isEqualTo(50L);
        assertThat(historial.tipoEvento()).isEqualTo(TipoEventoCuentaPorCobrar.CREACION);
        assertThat(historial.descripcion()).isEqualTo("Credito registrado por factura FAC-2026-00001 por valor de 3000");
        assertThat(historial.usuarioId()).isEqualTo(9L);
    }
}
