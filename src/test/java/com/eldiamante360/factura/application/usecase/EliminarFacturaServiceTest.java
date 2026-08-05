package com.eldiamante360.factura.application.usecase;

import com.eldiamante360.deudor.infrastructure.persistence.repository.CuentaPorCobrarJpaRepository;
import com.eldiamante360.factura.application.port.FacturaRepositoryPort;
import com.eldiamante360.factura.domain.model.DetalleFactura;
import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.Factura;
import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.domain.model.MedioPago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarFacturaServiceTest {

    @Mock
    private FacturaRepositoryPort facturaRepositoryPort;

    @Mock
    private CuentaPorCobrarJpaRepository cuentaPorCobrarJpaRepository;

    private EliminarFacturaService service;

    @BeforeEach
    void setUp() {
        service = new EliminarFacturaService(facturaRepositoryPort, cuentaPorCobrarJpaRepository);
    }

    private Factura factura(Long id, EstadoFactura estado) {
        DetalleFactura detalle = DetalleFactura.nuevo(5L, "Chorizo", BigDecimal.valueOf(2), BigDecimal.valueOf(1500), BigDecimal.ZERO);
        return new Factura(id, "FAC-2026-00001", 1L, "Juan Perez", "123456789", TipoPago.CONTADO,
                List.of(detalle), estado, BigDecimal.valueOf(3000), BigDecimal.ZERO, BigDecimal.valueOf(3000),
                9L, "Ana Gomez", Instant.now(), estado == EstadoFactura.ANULADA ? Instant.now() : null, 0,
                MedioPago.EFECTIVO);
    }

    @Test
    void ejecutar_conFacturaAnuladaSinCuentaPorCobrar_laElimina() {
        Factura anulada = factura(100L, EstadoFactura.ANULADA);
        when(facturaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(anulada));
        when(cuentaPorCobrarJpaRepository.existsByFacturaId(100L)).thenReturn(false);

        service.ejecutar(100L, false);

        verify(facturaRepositoryPort).eliminar(100L);
    }

    @Test
    void ejecutar_conFacturaEmitidaNoAnulada_lanzaExcepcion() {
        Factura emitida = factura(100L, EstadoFactura.EMITIDA);
        when(facturaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(emitida));

        assertThatThrownBy(() -> service.ejecutar(100L, false))
                .isInstanceOf(RecursoConDependenciasException.class)
                .hasMessageContaining("anulada");

        verify(facturaRepositoryPort, never()).eliminar(100L);
    }

    @Test
    void ejecutar_conCuentaPorCobrarAsociada_lanzaExcepcion() {
        Factura anulada = factura(100L, EstadoFactura.ANULADA);
        when(facturaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(anulada));
        when(cuentaPorCobrarJpaRepository.existsByFacturaId(100L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(100L, false))
                .isInstanceOf(RecursoConDependenciasException.class)
                .hasMessageContaining("cuenta por cobrar");

        verify(facturaRepositoryPort, never()).eliminar(100L);
    }

    @Test
    void ejecutar_conFacturaInexistente_lanzaExcepcion() {
        when(facturaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, false))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(facturaRepositoryPort, never()).eliminar(404L);
    }

    @Test
    void ejecutar_conCascadaTrueYCuentaPorCobrar_laBorraYEliminaFactura() {
        Factura anulada = factura(100L, EstadoFactura.ANULADA);
        when(facturaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(anulada));
        when(cuentaPorCobrarJpaRepository.existsByFacturaId(100L)).thenReturn(true);

        service.ejecutar(100L, true);

        verify(cuentaPorCobrarJpaRepository).deleteByFacturaId(100L);
        verify(facturaRepositoryPort).eliminar(100L);
    }

    @Test
    void ejecutar_conCascadaTrueYFacturaNoAnulada_sigueBloqueando() {
        Factura emitida = factura(100L, EstadoFactura.EMITIDA);
        when(facturaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(emitida));

        assertThatThrownBy(() -> service.ejecutar(100L, true))
                .isInstanceOf(RecursoConDependenciasException.class)
                .hasMessageContaining("anulada");

        verify(facturaRepositoryPort, never()).eliminar(100L);
    }
}
