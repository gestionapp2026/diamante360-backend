package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.formula.infrastructure.persistence.repository.DetalleFormulaJpaRepository;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import com.eldiamante360.insumoquimico.infrastructure.persistence.repository.MovimientoInsumoJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarInsumoQuimicoServiceTest {

    @Mock
    private InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    @Mock
    private MovimientoInsumoJpaRepository movimientoInsumoJpaRepository;

    @Mock
    private DetalleFormulaJpaRepository detalleFormulaJpaRepository;

    private EliminarInsumoQuimicoService service;

    private InsumoQuimico insumo;

    @BeforeEach
    void setUp() {
        service = new EliminarInsumoQuimicoService(insumoQuimicoRepositoryPort, movimientoInsumoJpaRepository,
                detalleFormulaJpaRepository);
        insumo = new InsumoQuimico(5L, "Sal de cura", UnidadMedidaInsumo.KG, BigDecimal.TEN, true, 0, null);
    }

    @Test
    void ejecutar_sinDependencias_loElimina() {
        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumo));
        when(movimientoInsumoJpaRepository.existsByInsumoId(5L)).thenReturn(false);
        when(detalleFormulaJpaRepository.existsByInsumoId(5L)).thenReturn(false);

        service.ejecutar(5L, false);

        verify(insumoQuimicoRepositoryPort).eliminar(5L);
    }

    @Test
    void ejecutar_conMovimientosAsociados_lanzaExcepcion() {
        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumo));
        when(movimientoInsumoJpaRepository.existsByInsumoId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, false))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(insumoQuimicoRepositoryPort, never()).eliminar(5L);
    }

    @Test
    void ejecutar_conFormulasAsociadas_lanzaExcepcion() {
        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumo));
        when(movimientoInsumoJpaRepository.existsByInsumoId(5L)).thenReturn(false);
        when(detalleFormulaJpaRepository.existsByInsumoId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.ejecutar(5L, false))
                .isInstanceOf(RecursoConDependenciasException.class);

        verify(insumoQuimicoRepositoryPort, never()).eliminar(5L);
    }

    @Test
    void ejecutar_conInsumoInexistente_lanzaExcepcion() {
        when(insumoQuimicoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, false))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(insumoQuimicoRepositoryPort, never()).eliminar(404L);
    }

    @Test
    void ejecutar_conCascadaTrueYDependencias_lasBorraYEliminaInsumo() {
        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumo));
        when(movimientoInsumoJpaRepository.existsByInsumoId(5L)).thenReturn(true);

        service.ejecutar(5L, true);

        verify(movimientoInsumoJpaRepository).deleteByInsumoId(5L);
        verify(detalleFormulaJpaRepository).deleteByInsumoId(5L);
        verify(insumoQuimicoRepositoryPort).eliminar(5L);
    }
}
