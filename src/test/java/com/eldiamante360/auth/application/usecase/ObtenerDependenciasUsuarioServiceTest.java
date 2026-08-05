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
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerDependenciasUsuarioServiceTest {

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

    private ObtenerDependenciasUsuarioService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        service = new ObtenerDependenciasUsuarioService(usuarioRepositoryPort, facturaJpaRepository,
                ordenJpaRepository, movimientoInventarioJpaRepository, movimientoInsumoJpaRepository,
                cuentaPorCobrarJpaRepository, abonoJpaRepository, historialCuentaPorCobrarJpaRepository,
                historialFacturaJpaRepository, historialClienteJpaRepository, observacionClienteJpaRepository);
        Rol rolAdmin = new Rol(1L, "ADMIN", "Administrador", Set.of());
        usuario = new Usuario(5L, "angie", "hash", "Angie", rolAdmin, true, false, null, 0);
    }

    private void sinDependencias() {
        when(facturaJpaRepository.countByUsuarioId(5L)).thenReturn(0L);
        when(ordenJpaRepository.countByUsuarioId(5L)).thenReturn(0L);
        when(movimientoInventarioJpaRepository.countByUsuarioId(5L)).thenReturn(0L);
        when(movimientoInsumoJpaRepository.countByUsuarioId(5L)).thenReturn(0L);
        when(cuentaPorCobrarJpaRepository.countByUsuarioId(5L)).thenReturn(0L);
        when(abonoJpaRepository.countByUsuarioId(5L)).thenReturn(0L);
        when(historialCuentaPorCobrarJpaRepository.countByUsuarioId(5L)).thenReturn(0L);
        when(historialFacturaJpaRepository.countByUsuarioId(5L)).thenReturn(0L);
        when(historialClienteJpaRepository.countByUsuarioId(5L)).thenReturn(0L);
        when(observacionClienteJpaRepository.countByUsuarioId(5L)).thenReturn(0L);
    }

    @Test
    void ejecutar_conDependencias_devuelveNoBloqueadoConConteosYTieneDependencias() {
        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        sinDependencias();
        when(facturaJpaRepository.countByUsuarioId(5L)).thenReturn(2L);
        when(movimientoInventarioJpaRepository.countByUsuarioId(5L)).thenReturn(1L);
        when(abonoJpaRepository.countByUsuarioId(5L)).thenReturn(3L);

        var resultado = service.ejecutar(5L, 1L);

        assertThat(resultado.tieneDependencias()).isTrue();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.mensajeBloqueo()).isNull();
        assertThat(resultado.dependencias()).hasSize(3);
    }

    @Test
    void ejecutar_sinDependencias_devuelveSinBloqueo() {
        when(usuarioRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(usuario));
        sinDependencias();

        var resultado = service.ejecutar(5L, 1L);

        assertThat(resultado.tieneDependencias()).isFalse();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.mensajeBloqueo()).isNull();
        assertThat(resultado.dependencias()).isEmpty();
    }

    @Test
    void ejecutar_conUsuarioAutenticadoActual_devuelveBloqueadoPorAutoeliminacionSinConsultarNada() {
        var resultado = service.ejecutar(1L, 1L);

        assertThat(resultado.bloqueado()).isTrue();
        assertThat(resultado.tieneDependencias()).isFalse();
        assertThat(resultado.mensajeBloqueo()).isEqualTo("No puedes eliminar tu propio usuario.");
        assertThat(resultado.dependencias()).isEmpty();

        verifyNoInteractions(facturaJpaRepository, ordenJpaRepository, movimientoInventarioJpaRepository,
                movimientoInsumoJpaRepository, cuentaPorCobrarJpaRepository, abonoJpaRepository,
                historialCuentaPorCobrarJpaRepository, historialFacturaJpaRepository, historialClienteJpaRepository,
                observacionClienteJpaRepository);
    }

    @Test
    void ejecutar_conUsuarioInexistente_lanzaExcepcion() {
        when(usuarioRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, 1L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
