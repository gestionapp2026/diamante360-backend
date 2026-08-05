package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.dto.CrearClienteCommand;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.HistorialClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.exception.NumeroDocumentoDuplicadoException;
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
class CrearClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private RutaRepositoryPort rutaRepositoryPort;

    @Mock
    private HistorialClienteRepositoryPort historialClienteRepositoryPort;

    private CrearClienteService service;

    @BeforeEach
    void setUp() {
        service = new CrearClienteService(clienteRepositoryPort, rutaRepositoryPort, historialClienteRepositoryPort);
    }

    @Test
    void ejecutar_conDocumentoLibreYSinRuta_creaElClienteYRegistraHistorial() {
        CrearClienteCommand command = new CrearClienteCommand(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                "3001234567", "juan@correo.com", "Calle 1 # 2-3", null, 1L);
        when(clienteRepositoryPort.existePorNumeroDocumento("123456789")).thenReturn(false);
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenAnswer(invocacion -> {
            Cliente c = invocacion.getArgument(0);
            return new Cliente(10L, c.getTipoDocumento(), c.getNumeroDocumento(), c.getNombre(), c.getTelefono(),
                    c.getEmail(), c.getDireccion(), c.getRuta(), c.isActivo(), 0);
        });

        ClienteResult resultado = service.ejecutar(command);

        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.nombre()).isEqualTo("Juan Perez");
        assertThat(resultado.activo()).isTrue();
        assertThat(resultado.rutaId()).isNull();

        ArgumentCaptor<HistorialCliente> captor = ArgumentCaptor.forClass(HistorialCliente.class);
        verify(historialClienteRepositoryPort).guardar(captor.capture());
        assertThat(captor.getValue().tipoEvento()).isEqualTo(TipoEventoCliente.CREACION);
        assertThat(captor.getValue().clienteId()).isEqualTo(10L);
    }

    @Test
    void ejecutar_conRutaActiva_asignaLaRuta() {
        Ruta ruta = new Ruta(2L, "Ruta Norte", "Zona norte", true);
        CrearClienteCommand command = new CrearClienteCommand(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                "3001234567", "juan@correo.com", "Calle 1 # 2-3", 2L, 1L);
        when(clienteRepositoryPort.existePorNumeroDocumento("123456789")).thenReturn(false);
        when(rutaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(ruta));
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenAnswer(invocacion -> {
            Cliente c = invocacion.getArgument(0);
            return new Cliente(10L, c.getTipoDocumento(), c.getNumeroDocumento(), c.getNombre(), c.getTelefono(),
                    c.getEmail(), c.getDireccion(), c.getRuta(), c.isActivo(), 0);
        });

        ClienteResult resultado = service.ejecutar(command);

        assertThat(resultado.rutaId()).isEqualTo(2L);
        assertThat(resultado.rutaNombre()).isEqualTo("Ruta Norte");
    }

    @Test
    void ejecutar_conDocumentoYaExistente_lanzaExcepcion() {
        CrearClienteCommand command = new CrearClienteCommand(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                "3001234567", "juan@correo.com", "Calle 1 # 2-3", null, 1L);
        when(clienteRepositoryPort.existePorNumeroDocumento("123456789")).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(NumeroDocumentoDuplicadoException.class);
    }

    @Test
    void ejecutar_conRutaInexistente_lanzaExcepcion() {
        CrearClienteCommand command = new CrearClienteCommand(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                "3001234567", "juan@correo.com", "Calle 1 # 2-3", 404L, 1L);
        when(clienteRepositoryPort.existePorNumeroDocumento("123456789")).thenReturn(false);
        when(rutaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void ejecutar_conRutaInactiva_lanzaExcepcion() {
        Ruta rutaInactiva = new Ruta(2L, "Ruta Norte", "Zona norte", false);
        CrearClienteCommand command = new CrearClienteCommand(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                "3001234567", "juan@correo.com", "Calle 1 # 2-3", 2L, 1L);
        when(clienteRepositoryPort.existePorNumeroDocumento("123456789")).thenReturn(false);
        when(rutaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(rutaInactiva));

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RutaInactivaException.class);
    }
}
