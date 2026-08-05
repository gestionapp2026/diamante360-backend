package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.application.port.RutaRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.Ruta;
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
class ListarClientesPorRutaServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private RutaRepositoryPort rutaRepositoryPort;

    private ListarClientesPorRutaService service;

    @BeforeEach
    void setUp() {
        service = new ListarClientesPorRutaService(clienteRepositoryPort, rutaRepositoryPort);
    }

    @Test
    void ejecutar_conRutaExistente_retornaLaPaginaMapeada() {
        Ruta ruta = new Ruta(2L, "Ruta Norte", "Zona norte", true);
        Cliente cliente = new Cliente(5L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", "3001234567",
                "juan@correo.com", "Calle 1 # 2-3", ruta, true, 0);
        Pageable pageable = PageRequest.of(0, 10);
        when(rutaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(ruta));
        when(clienteRepositoryPort.listarPorRuta(2L, pageable)).thenReturn(new PageImpl<>(List.of(cliente)));

        Page<ClienteResult> resultado = service.ejecutar(2L, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).rutaId()).isEqualTo(2L);
    }

    @Test
    void ejecutar_conRutaInexistente_lanzaExcepcion() {
        Pageable pageable = PageRequest.of(0, 10);
        when(rutaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, pageable))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
