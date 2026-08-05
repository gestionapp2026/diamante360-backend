package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.formula.infrastructure.persistence.repository.DetalleFormulaJpaRepository;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import com.eldiamante360.insumoquimico.infrastructure.persistence.repository.MovimientoInsumoJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerDependenciasInsumoQuimicoServiceTest {

    @Mock
    private InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    @Mock
    private MovimientoInsumoJpaRepository movimientoInsumoJpaRepository;

    @Mock
    private DetalleFormulaJpaRepository detalleFormulaJpaRepository;

    private ObtenerDependenciasInsumoQuimicoService service;

    private InsumoQuimico insumo;

    @BeforeEach
    void setUp() {
        service = new ObtenerDependenciasInsumoQuimicoService(insumoQuimicoRepositoryPort,
                movimientoInsumoJpaRepository, detalleFormulaJpaRepository);
        insumo = new InsumoQuimico(5L, "Sal de cura", UnidadMedidaInsumo.KG, BigDecimal.TEN, true, 0, null);
    }

    @Test
    void ejecutar_conDependencias_devuelveConteosYNuncaBloquea() {
        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumo));
        when(movimientoInsumoJpaRepository.countByInsumoId(5L)).thenReturn(4L);
        when(detalleFormulaJpaRepository.countByInsumoId(5L)).thenReturn(2L);

        var resultado = service.ejecutar(5L);

        assertThat(resultado.tieneDependencias()).isTrue();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.mensajeBloqueo()).isNull();
        assertThat(resultado.dependencias()).hasSize(2);
    }

    @Test
    void ejecutar_sinDependencias_devuelveVacio() {
        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumo));
        when(movimientoInsumoJpaRepository.countByInsumoId(5L)).thenReturn(0L);
        when(detalleFormulaJpaRepository.countByInsumoId(5L)).thenReturn(0L);

        var resultado = service.ejecutar(5L);

        assertThat(resultado.tieneDependencias()).isFalse();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.dependencias()).isEmpty();
    }

    @Test
    void ejecutar_conInsumoInexistente_lanzaExcepcion() {
        when(insumoQuimicoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
