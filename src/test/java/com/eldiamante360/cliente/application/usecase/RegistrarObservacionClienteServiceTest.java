package com.eldiamante360.cliente.application.usecase;

import java.util.List;
import com.eldiamante360.cliente.application.dto.ObservacionClienteResult;
import com.eldiamante360.cliente.application.dto.RegistrarObservacionClienteCommand;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.ObservacionClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.ObservacionCliente;
import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarObservacionClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private ObservacionClienteRepositoryPort observacionClienteRepositoryPort;

    private RegistrarObservacionClienteService service;

    @BeforeEach
    void setUp() {
        service = new RegistrarObservacionClienteService(clienteRepositoryPort, observacionClienteRepositoryPort);
    }

    @Test
    void ejecutar_conClienteExistente_registraLaObservacion() {
        Cliente cliente = new Cliente(5L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", List.of("3001234567"),
                "juan@correo.com", "Calle 1 # 2-3", null, true, 0);
        RegistrarObservacionClienteCommand command = new RegistrarObservacionClienteCommand(5L,
                "Cliente prefiere entregas en la manana", 1L);
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(observacionClienteRepositoryPort.guardar(any(ObservacionCliente.class))).thenAnswer(invocacion -> {
            ObservacionCliente o = invocacion.getArgument(0);
            return new ObservacionCliente(20L, o.clienteId(), o.texto(), o.usuarioId(), Instant.now());
        });

        ObservacionClienteResult resultado = service.ejecutar(command);

        assertThat(resultado.id()).isEqualTo(20L);
        assertThat(resultado.clienteId()).isEqualTo(5L);
        assertThat(resultado.texto()).isEqualTo("Cliente prefiere entregas en la manana");
    }

    @Test
    void ejecutar_conClienteInexistente_lanzaExcepcion() {
        RegistrarObservacionClienteCommand command = new RegistrarObservacionClienteCommand(404L, "Observacion", 1L);
        when(clienteRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(command))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
