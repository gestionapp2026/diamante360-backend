package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ObservacionClienteResult;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarObservacionesClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private ObservacionClienteRepositoryPort observacionClienteRepositoryPort;

    private ListarObservacionesClienteService service;

    @BeforeEach
    void setUp() {
        service = new ListarObservacionesClienteService(clienteRepositoryPort, observacionClienteRepositoryPort);
    }

    @Test
    void ejecutar_conClienteExistente_retornaLaPaginaMapeada() {
        Cliente cliente = new Cliente(5L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", List.of("3001234567"),
                "juan@correo.com", "Calle 1 # 2-3", null, true, 0);
        ObservacionCliente observacion = ObservacionCliente.nueva(5L, "Observacion", 1L);
        Pageable pageable = PageRequest.of(0, 10);
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(observacionClienteRepositoryPort.listarPorCliente(5L, pageable)).thenReturn(new PageImpl<>(List.of(observacion)));

        Page<ObservacionClienteResult> resultado = service.ejecutar(5L, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).texto()).isEqualTo("Observacion");
    }

    @Test
    void ejecutar_conClienteInexistente_lanzaExcepcion() {
        Pageable pageable = PageRequest.of(0, 10);
        when(clienteRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, pageable))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
