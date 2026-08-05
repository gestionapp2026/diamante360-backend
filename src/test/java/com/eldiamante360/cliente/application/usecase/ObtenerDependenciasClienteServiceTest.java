package com.eldiamante360.cliente.application.usecase;

import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
import com.eldiamante360.deudor.infrastructure.persistence.repository.CuentaPorCobrarJpaRepository;
import com.eldiamante360.factura.infrastructure.persistence.repository.FacturaJpaRepository;
import com.eldiamante360.orden.infrastructure.persistence.repository.OrdenJpaRepository;
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
class ObtenerDependenciasClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private FacturaJpaRepository facturaJpaRepository;

    @Mock
    private OrdenJpaRepository ordenJpaRepository;

    @Mock
    private CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository;

    private ObtenerDependenciasClienteService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        service = new ObtenerDependenciasClienteService(clienteRepositoryPort, facturaJpaRepository,
                ordenJpaRepository, cuentaPorCobrarJpaRepository);
        cliente = new Cliente(5L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", "3001234567",
                "juan@correo.com", "Calle 1 # 2-3", null, true, 0);
    }

    @Test
    void ejecutar_conDependencias_devuelveConteosSinBloqueo() {
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(facturaJpaRepository.countByClienteId(5L)).thenReturn(2L);
        when(ordenJpaRepository.countByClienteId(5L)).thenReturn(1L);
        when(cuentaPorCobrarJpaRepository.countByClienteId(5L)).thenReturn(3L);

        var resultado = service.ejecutar(5L);

        assertThat(resultado.tieneDependencias()).isTrue();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.mensajeBloqueo()).isNull();
        assertThat(resultado.dependencias()).hasSize(3);
    }

    @Test
    void ejecutar_sinDependencias_devuelveVacioSinBloqueo() {
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(facturaJpaRepository.countByClienteId(5L)).thenReturn(0L);
        when(ordenJpaRepository.countByClienteId(5L)).thenReturn(0L);
        when(cuentaPorCobrarJpaRepository.countByClienteId(5L)).thenReturn(0L);

        var resultado = service.ejecutar(5L);

        assertThat(resultado.tieneDependencias()).isFalse();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.dependencias()).isEmpty();
    }

    @Test
    void ejecutar_conClienteInexistente_lanzaExcepcion() {
        when(clienteRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
