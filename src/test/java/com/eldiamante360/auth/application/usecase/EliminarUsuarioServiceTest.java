package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.port.UsuarioRepositoryPort;
import com.eldiamante360.auth.domain.model.Rol;
import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.cliente.infrastructure.persistence.repository.HistorialClienteJpaRepository;
import com.eldiamante360.cliente.infrastructure.persistence.repository.ObservacionClienteJpaRepository;
import com.eldiamante360.deudor.infrastructure.persistence.repository.AbonoJpaRepository;
import com.eldiamante360.deudor.infrastructure.persistence.repository.CuentaPorCobrarJpaRepository;
import com.eldiamante360.deudor.infrastructure.persistence.repository.HistorialCuentaPorCobrarJpaRepository;
import com.eldiamante360.factura.infrastructure.persistence.repository.FacturaJpaRepository;
import com.eldiamante360.factura.infrastructure.persistence.repository.HistorialFacturaJpaRepository;
import com.eldiamante360.insumoquimico.infrastructure.persistence.repository.MovimientoInsumoJpaRepository;
import com.eldiamante360.inventario.infrastructure.persistence.repository.MovimientoInventarioJpaRepository;
import com.eldiamante360.orden.infrastructure.persistence.repository.OrdenJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarUsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private FacturaJpaRepository facturaJpaRepository;

    @Mock
    private OrdenJpaRepository ordenJpaRepository;

    @Mock
    private MovimientoInventarioJpaRepository movimientoInventarioJpaRepository;

    @Mock
    private MovimientoInsumoJpaRepository movimientoInsumoJpaRepository;

    @Mock
    private CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository;

    @Mock
    private AbonoJpaRepository abonoJpaRepository;

    @Mock
    private HistorialCuentaPorCobrarJpaRepository historialCuentaPorCobrarJpaRepository;

    @Mock
    private HistorialFacturaJpaRepository historialFacturaJpaRepository;

    @Mock
    private HistorialClienteJpaRepository historialClienteJpaRepository;

    @Mock
    private ObservacionClienteJpaRepository observacionClienteJpaRepository;

    private EliminarUsuarioService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        service = new EliminarUsuarioService(usuarioRepositoryPort, facturaJpaRepository, ordenJpaRepository,
                movimientoInventarioJpaRepository, movimientoInsumoJpaRepository, cuentaPorCobrarJpaRepository,
                abonoJpaRepository, historialCuentaPorCobrarJpaRepository, historialFacturaJpaRepository,
                historialClienteJpaRepository, observacionClienteJpaRepository);
        Rol rolAdmin = new Rol(1L, "ADMIN", "Administrador", Set.of());
        usuario = new Usuario(5L, "angie", "hash", "Angie", rolAdmin, true, false, null, 0);
    }

    private void sinDependencias() {
        when(facturaJpaRepository.existsByUsuarioId(5L)).thenReturn(false);
        when(ordenJpaRepository.existsByUsuarioId(5L)).thenReturn(false);
        when(movimientoInventarioJpaRepository.existsByUsuarioId(5L)).thenReturn(false);
        when(movimientoInsumoJpaRepository.existsByUsuarioId(5L)).thenReturn(false);
        when(cuentaPorCobrarJpaRepository.existsByUsuarioId(5L)).thenReturn(false);
        when(abonoJpaRepository.existsByUsuarioId(5L)).thenReturn(false);
        when(historialCuentaPorCobrarJpaRepository.existsByUsuarioId(5L)).thenReturn(false);
        when(historialFacturaJpaRepository.existsByUsuarioId(5L)).thenReturn(false);
        when(historialClienteJpaRepository.existsByUsuarioId(5L)).thenReturn(false);
        when(observacionClienteJpaRepository.existsByUsuarioId(5L)).thenReturn(false);
    }

    @Test
    void ejecutar_sinDependencias_loElimina() {
        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        sinDependencias();

        service.ejecutar(5L, 1L, false);

        verify(usuarioRepositoryPort).eliminar(5L);
    }

    @Test
    void ejecutar_intentaEliminarsePropioUsuario_lanzaExcepcionYNoConsultaNada() {
        assertThatThrownBy(() -> service.ejecutar(1L, 1L, false))
                .isInstanceOf(RecursoConDependenciasException.class)
                .hasMessageContaining("propio usuario");

        verifyNoInteractions(facturaJpaRepository, ordenJpaRepository, movimientoInventarioJpaRepository,
                movimientoInsumoJpaRepository, cuentaPorCobrarJpaRepository, abonoJpaRepository,
                historialCuentaPorCobrarJpaRepository, historialFacturaJpaRepository, historialClienteJpaRepository,
                observacionClienteJpaRepository);
        verify(usuarioRepositoryPort, never()).buscarPorId(any());
        verify(usuarioRepositoryPort, never()).eliminar(any());
    }

    @Test
    void ejecutar_conFacturasAsociadasSinCascada_lanzaExcepcion() {
        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        when(facturaJpaRepository.existsByUsuarioId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, 1L, false))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(usuarioRepositoryPort, never()).eliminar(5L);
        verify(facturaJpaRepository, never()).deleteByUsuarioId(any());
    }

    @Test
    void ejecutar_conUsuarioInexistente_lanzaExcepcion() {
        when(usuarioRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, 1L, false))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(usuarioRepositoryPort, never()).eliminar(404L);
    }

    @Test
    void ejecutar_conDependenciasYCascadaTrue_borraTodoEnCascadaYLuegoElUsuario() {
        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        when(facturaJpaRepository.existsByUsuarioId(5L)).thenReturn(true);

        service.ejecutar(5L, 1L, true);

        verify(cuentaPorCobrarJpaRepository).deleteByFacturaUsuarioId(5L);
        verify(cuentaPorCobrarJpaRepository).deleteByUsuarioId(5L);
        verify(abonoJpaRepository).deleteByUsuarioId(5L);
        verify(historialCuentaPorCobrarJpaRepository).deleteByUsuarioId(5L);
        verify(historialFacturaJpaRepository).deleteByUsuarioId(5L);
        verify(facturaJpaRepository).deleteByUsuarioId(5L);
        verify(ordenJpaRepository).deleteByUsuarioId(5L);
        verify(movimientoInventarioJpaRepository).deleteByUsuarioId(5L);
        verify(movimientoInsumoJpaRepository).deleteByUsuarioId(5L);
        verify(historialClienteJpaRepository).deleteByUsuarioId(5L);
        verify(observacionClienteJpaRepository).deleteByUsuarioId(5L);
        verify(usuarioRepositoryPort).eliminar(5L);
    }

    @Test
    void ejecutar_sinDependenciasYCascadaTrue_igualLoElimina() {
        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        sinDependencias();

        service.ejecutar(5L, 1L, true);

        verify(usuarioRepositoryPort).eliminar(5L);
    }
}
