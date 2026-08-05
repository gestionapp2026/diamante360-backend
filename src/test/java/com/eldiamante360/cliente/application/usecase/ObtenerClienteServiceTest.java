package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    private ObtenerClienteService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerClienteService(clienteRepositoryPort);
    }

    @Test
    void ejecutar_conClienteExistente_retornaElCliente() {
        Cliente cliente = new Cliente(5L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", "3001234567",
                "juan@correo.com", "Calle 1 # 2-3", null, true, 0);
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));

        ClienteResult resultado = service.ejecutar(5L);

        assertThat(resultado.id()).isEqualTo(5L);
        assertThat(resultado.nombre()).isEqualTo("Juan Perez");
    }

    @Test
    void ejecutar_conClienteInexistente_lanzaExcepcion() {
        when(clienteRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
