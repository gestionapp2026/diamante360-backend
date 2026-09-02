package com.eldiamante360.cliente.application.usecase;

import java.util.List;
import com.eldiamante360.cliente.application.dto.AsignarRutaClienteCommand;
import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.HistorialClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.exception.RutaInactivaException;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.HistorialCliente;
import com.eldiamante360.cliente.domain.model.Ruta;
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
class AsignarRutaClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private RutaRepositoryPort rutaRepositoryPort;

    @Mock
    private HistorialClienteRepositoryPort historialClienteRepositoryPort;

    private AsignarRutaClienteService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        service = new AsignarRutaClienteService(clienteRepositoryPort, rutaRepositoryPort, historialClienteRepositoryPort);
        cliente = new Cliente(5L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", List.of("3001234567"),
                "juan@correo.com", "Calle 1 # 2-3", null, true, 0);
    }

    @Test
    void ejecutar_conRutaIdPresenteYActiva_asignaLaRutaYRegistraHistorial() {
        Ruta ruta = new Ruta(2L, "Ruta Norte", "Zona norte", true);
        AsignarRutaClienteCommand command = new AsignarRutaClienteCommand(5L, 2L, 1L);
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(rutaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(ruta));
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        ClienteResult resultado = service.ejecutar(command);

        assertThat(resultado.rutaId()).isEqualTo(2L);
        assertThat(resultado.rutaNombre()).isEqualTo("Ruta Norte");

        ArgumentCaptor<HistorialCliente> captor = ArgumentCaptor.forClass(HistorialCliente.class);
        verify(historialClienteRepositoryPort).guardar(captor.capture());
        assertThat(captor.getValue().tipoEvento()).isEqualTo(TipoEventoCliente.CAMBIO_RUTA);
        assertThat(captor.getValue().descripcion()).contains("asignada");
    }

    @Test
    void ejecutar_conRutaIdNulo_removueLaRutaYRegistraHistorial() {
        Ruta ruta = new Ruta(2L, "Ruta Norte", "Zona norte", true);
        Cliente clienteConRuta = new Cliente(5L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", List.of("3001234567"),
                "juan@correo.com", "Calle 1 # 2-3", ruta, true, 0);
        AsignarRutaClienteCommand command = new AsignarRutaClienteCommand(5L, null, 1L);
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(clienteConRuta));
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        ClienteResult resultado = service.ejecutar(command);

        assertThat(resultado.rutaId()).isNull();

        ArgumentCaptor<HistorialCliente> captor = ArgumentCaptor.forClass(HistorialCliente.class);
        verify(historialClienteRepositoryPort).guardar(captor.capture());
        assertThat(captor.getValue().tipoEvento()).isEqualTo(TipoEventoCliente.CAMBIO_RUTA);
        assertThat(captor.getValue().descripcion()).contains("removida");
    }

    @Test
    void ejecutar_conClienteInexistente_lanzaExcepcion() {
        AsignarRutaClienteCommand command = new AsignarRutaClienteCommand(404L, 2L, 1L);
        when(clienteRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void ejecutar_conRutaInexistente_lanzaExcepcion() {
        AsignarRutaClienteCommand command = new AsignarRutaClienteCommand(5L, 404L, 1L);
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(rutaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void ejecutar_conRutaInactiva_lanzaExcepcion() {
        Ruta rutaInactiva = new Ruta(2L, "Ruta Norte", "Zona norte", false);
        AsignarRutaClienteCommand command = new AsignarRutaClienteCommand(5L, 2L, 1L);
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(rutaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(rutaInactiva));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RutaInactivaException.class);
    }
}
