package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.orden.application.port.OrdenRepositoryPort;
import com.eldiamante360.orden.domain.model.DetalleOrden;
import com.eldiamante360.orden.domain.model.EstadoOrden;
import com.eldiamante360.orden.domain.model.Orden;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarOrdenServiceTest {

    @Mock
    private OrdenRepositoryPort ordenRepositoryPort;

    private EliminarOrdenService service;

    @BeforeEach
    void setUp() {
        service = new EliminarOrdenService(ordenRepositoryPort);
    }

    private Orden orden(Long id, EstadoOrden estado) {
        DetalleOrden detalle = new DetalleOrden(1L, 5L, "Chorizo", BigDecimal.valueOf(2));
        return new Orden(id, "OR-2026-00001", 1L, "Juan Perez", Instant.now(), LocalDate.now().plusDays(3),
                estado, "Observaciones", List.of(detalle), 9L, "Ana Gomez",
                estado == EstadoOrden.DESPACHADA ? Instant.now() : null,
                estado == EstadoOrden.ANULADA ? Instant.now() : null, 0);
    }

    @Test
    void ejecutar_conOrdenAnulada_laElimina() {
        Orden anulada = orden(100L, EstadoOrden.ANULADA);
        when(ordenRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(anulada));

        service.ejecutar(100L, false);

        verify(ordenRepositoryPort).eliminar(100L);
    }

    @Test
    void ejecutar_conOrdenDespachada_laElimina() {
        Orden despachada = orden(100L, EstadoOrden.DESPACHADA);
        when(ordenRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(despachada));

        service.ejecutar(100L, false);

        verify(ordenRepositoryPort).eliminar(100L);
    }

    @Test
    void ejecutar_conOrdenPendiente_lanzaExcepcion() {
        Orden pendiente = orden(100L, EstadoOrden.PENDIENTE);
        when(ordenRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(pendiente));

        assertThatThrownBy(() -> service.ejecutar(100L, false))
                .isInstanceOf(RecursoConDependenciasException.class)
                .hasMessageContaining("pendiente");

        verify(ordenRepositoryPort, never()).eliminar(100L);
    }

    @Test
    void ejecutar_conOrdenInexistente_lanzaExcepcion() {
        when(ordenRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, false))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(ordenRepositoryPort, never()).eliminar(404L);
    }

    @Test
    void ejecutar_conCascadaTrueYOrdenPendiente_sigueBloqueando() {
        Orden pendiente = orden(100L, EstadoOrden.PENDIENTE);
        when(ordenRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(pendiente));

        assertThatThrownBy(() -> service.ejecutar(100L, true))
                .isInstanceOf(RecursoConDependenciasException.class)
                .hasMessageContaining("pendiente");

        verify(ordenRepositoryPort, never()).eliminar(100L);
    }
}
