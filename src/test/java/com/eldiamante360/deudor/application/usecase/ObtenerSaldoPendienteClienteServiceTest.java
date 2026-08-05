package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
import com.eldiamante360.deudor.application.dto.SaldoClienteResult;
import com.eldiamante360.deudor.application.port.CuentaPorCobrarRepositoryPort;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerSaldoPendienteClienteServiceTest {

    @Mock
    private CuentaPorCobrarRepositoryPort cuentaPorCobrarRepositoryPort;

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    private ObtenerSaldoPendienteClienteService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerSaldoPendienteClienteService(cuentaPorCobrarRepositoryPort, clienteRepositoryPort);
    }

    @Test
    void ejecutar_conClienteExistente_retornaElSaldoSumado() {
        Cliente cliente = new Cliente(1L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", "3001234567",
                "juan@correo.com", "Calle 1 # 2-3", null, true, 0);
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(cuentaPorCobrarRepositoryPort.sumarSaldoPendientePorCliente(1L)).thenReturn(BigDecimal.valueOf(1500));

        SaldoClienteResult resultado = service.ejecutar(1L);

        assertThat(resultado.clienteId()).isEqualTo(1L);
        assertThat(resultado.saldoPendiente()).isEqualByComparingTo(BigDecimal.valueOf(1500));
    }

    @Test
    void ejecutar_conClienteInexistente_lanzaExcepcionYNoInteractuaConCuentasPorCobrar() {
        when(clienteRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verifyNoInteractions(cuentaPorCobrarRepositoryPort);
    }
}
