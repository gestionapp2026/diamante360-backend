package com.eldiamante360.cliente.application.usecase;

import java.util.List;
import com.eldiamante360.cliente.application.port.ClienteRepositoryPort;
import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;
import com.eldiamante360.deudor.infrastructure.persistence.repository.CuentaPorCobrarJpaRepository;
import com.eldiamante360.factura.infrastructure.persistence.repository.FacturaJpaRepository;
import com.eldiamante360.orden.infrastructure.persistence.repository.OrdenJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private FacturaJpaRepository facturaJpaRepository;

    @Mock
    private OrdenJpaRepository ordenJpaRepository;

    @Mock
    private CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository;

    private EliminarClienteService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        service = new EliminarClienteService(clienteRepositoryPort, facturaJpaRepository, ordenJpaRepository,
                cuentaPorCobrarJpaRepository);
        cliente = new Cliente(5L, TipoDocumentoCliente.CC, "123456789", "Juan Perez", List.of("3001234567"),
                "juan@correo.com", "Calle 1 # 2-3", null, true, 0);
    }

    @Test
    void ejecutar_sinDependencias_loElimina() {
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(facturaJpaRepository.existsByClienteId(5L)).thenReturn(false);
        when(ordenJpaRepository.existsByClienteId(5L)).thenReturn(false);
        when(cuentaPorCobrarJpaRepository.existsByClienteId(5L)).thenReturn(false);

        service.ejecutar(5L, false);

        verify(clienteRepositoryPort).eliminar(5L);
    }

    @Test
    void ejecutar_conFacturasAsociadas_lanzaExcepcion() {
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(facturaJpaRepository.existsByClienteId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, false))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(clienteRepositoryPort, never()).eliminar(5L);
    }

    @Test
    void ejecutar_conOrdenesAsociadas_lanzaExcepcion() {
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(facturaJpaRepository.existsByClienteId(5L)).thenReturn(false);
        when(ordenJpaRepository.existsByClienteId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, false))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(clienteRepositoryPort, never()).eliminar(5L);
    }

    @Test
    void ejecutar_conCuentasPorCobrarAsociadas_lanzaExcepcion() {
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(facturaJpaRepository.existsByClienteId(5L)).thenReturn(false);
        when(ordenJpaRepository.existsByClienteId(5L)).thenReturn(false);
        when(cuentaPorCobrarJpaRepository.existsByClienteId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, false))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(clienteRepositoryPort, never()).eliminar(5L);
    }

    @Test
    void ejecutar_conClienteInexistente_lanzaExcepcion() {
        when(clienteRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, false))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(clienteRepositoryPort, never()).eliminar(404L);
    }

    @Test
    void ejecutar_conCascadaTrueYDependencias_lasBorraYEliminaCliente() {
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(facturaJpaRepository.existsByClienteId(5L)).thenReturn(true);

        service.ejecutar(5L, true);

        verify(cuentaPorCobrarJpaRepository).deleteByClienteId(5L);
        verify(facturaJpaRepository).deleteByClienteId(5L);
        verify(ordenJpaRepository).deleteByClienteId(5L);
        verify(clienteRepositoryPort).eliminar(5L);
    }

    @Test
    void ejecutar_conCascadaTrueSinDependencias_noIntentaBorrarNada() {
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(facturaJpaRepository.existsByClienteId(5L)).thenReturn(false);
        when(ordenJpaRepository.existsByClienteId(5L)).thenReturn(false);
        when(cuentaPorCobrarJpaRepository.existsByClienteId(5L)).thenReturn(false);

        service.ejecutar(5L, true);

        verify(cuentaPorCobrarJpaRepository).deleteByClienteId(5L);
        verify(facturaJpaRepository).deleteByClienteId(5L);
        verify(ordenJpaRepository).deleteByClienteId(5L);
        verify(clienteRepositoryPort).eliminar(5L);
    }
}
