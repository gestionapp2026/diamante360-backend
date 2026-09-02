package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarClientesServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    private ListarClientesService service;

    @BeforeEach
    void setUp() {
        service = new ListarClientesService(clienteRepositoryPort);
    }

    @Test
    void ejecutar_retornaLaPaginaMapeada() {
        Cliente cliente = new Cliente(5L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", List.of("3001234567"),
                "juan@correo.com", "Calle 1 # 2-3", null, true, 0);
        Pageable pageable = PageRequest.of(0, 10);
        when(clienteRepositoryPort.listar(pageable)).thenReturn(new PageImpl<>(List.of(cliente)));

        Page<ClienteResult> resultado = service.ejecutar(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nombre()).isEqualTo("Juan Perez");
    }
}
