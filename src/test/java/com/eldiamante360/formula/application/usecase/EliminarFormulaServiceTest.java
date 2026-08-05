package com.eldiamante360.formula.application.usecase;

import com.eldiamante360.formula.application.port.FormulaRepositoryPort;
import com.eldiamante360.formula.domain.model.DetalleFormula;
import com.eldiamante360.formula.domain.model.Formula;
import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarFormulaServiceTest {

    @Mock
    private FormulaRepositoryPort formulaRepositoryPort;

    private EliminarFormulaService service;

    @BeforeEach
    void setUp() {
        service = new EliminarFormulaService(formulaRepositoryPort);
    }

    @Test
    void ejecutar_conFormulaExistente_laElimina() {
        DetalleFormula detalle = DetalleFormula.nuevo(5L, 1, BigDecimal.TEN);
        Formula formula = new Formula(100L, 10L, "Chorizo", BigDecimal.valueOf(50), UnidadMedidaInsumo.KG,
                true, 0, List.of(detalle));
        when(formulaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(formula));

        service.ejecutar(100L, false);

        verify(formulaRepositoryPort).eliminar(100L);
    }

    @Test
    void ejecutar_conFormulaInexistente_lanzaExcepcion() {
        when(formulaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, false))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(formulaRepositoryPort, never()).eliminar(404L);
    }
}
