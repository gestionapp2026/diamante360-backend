package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.HistorialClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.HistorialCliente;
import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
import com.eldiamante360.cliente.domain.model.TipoEventoCliente;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private HistorialClienteRepositoryPort historialClienteRepositoryPort;

    private CambiarEstadoClienteService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        service = new CambiarEstadoClienteService(clienteRepositoryPort, historialClienteRepositoryPort);
        cliente = new Cliente(5L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", "3001234567",
                "juan@correo.com", "Calle 1 # 2-3", null, true, 0);
    }

    @Test
    void ejecutar_conActivoFalso_desactivaElClienteYRegistraHistorial() {
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        ClienteResult resultado = service.ejecutar(5L, false, 1L);

        assertThat(resultado.activo()).isFalse();

        ArgumentCaptor<HistorialCliente> captor = ArgumentCaptor.forClass(HistorialCliente.class);
        verify(historialClienteRepositoryPort).guardar(captor.capture());
        assertThat(captor.getValue().tipoEvento()).isEqualTo(TipoEventoCliente.DESACTIVACION);
    }

    @Test
    void ejecutar_conActivoTrue_activaElClienteYRegistraHistorial() {
        cliente.desactivar();
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        ClienteResult resultado = service.ejecutar(5L, true, 1L);

        assertThat(resultado.activo()).isTrue();

        ArgumentCaptor<HistorialCliente> captor = ArgumentCaptor.forClass(HistorialCliente.class);
        verify(historialClienteRepositoryPort).guardar(captor.capture());
        assertThat(captor.getValue().tipoEvento()).isEqualTo(TipoEventoCliente.ACTIVACION);
    }

    @Test
    void ejecutar_conClienteInexistente_lanzaExcepcion() {
        when(clienteRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, true, 1L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
