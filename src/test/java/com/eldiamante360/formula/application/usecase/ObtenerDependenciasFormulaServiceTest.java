package com.eldiamante360.formula.application.usecase;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerDependenciasFormulaServiceTest {

    @Mock
    private com.eldiamante360.formula.application.port.FormulaRepositoryPort formulaRepositoryPort;

    private ObtenerDependenciasFormulaService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerDependenciasFormulaService(formulaRepositoryPort);
    }

    @Test
    void ejecutar_conFormulaExistente_siempreDevuelveSinDependenciasNiBloqueo() {
        DetalleFormula detalle = DetalleFormula.nuevo(5L, 1, BigDecimal.TEN);
        Formula formula = new Formula(100L, 10L, "Chorizo", BigDecimal.valueOf(50), UnidadMedidaInsumo.KG,
                true, 0, List.of(detalle));
        when(formulaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(formula));

        var resultado = service.ejecutar(100L);

        assertThat(resultado.tieneDependencias()).isFalse();
        assertThat(resultado.bloqueado()).isFalse();
        assertThat(resultado.mensajeBloqueo()).isNull();
        assertThat(resultado.dependencias()).isEmpty();
    }

    @Test
    void ejecutar_conFormulaInexistente_lanzaExcepcion() {
        when(formulaRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
