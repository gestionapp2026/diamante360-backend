package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoInsumoQuimicoServiceTest {

    @Mock
    private InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    private CambiarEstadoInsumoQuimicoService service;

    private InsumoQuimico insumo;

    @BeforeEach
    void setUp() {
        service = new CambiarEstadoInsumoQuimicoService(insumoQuimicoRepositoryPort);
        insumo = new InsumoQuimico(5L, "Sal de cura", UnidadMedidaInsumo.KG, BigDecimal.TEN, true, 0, null);
    }

    @Test
    void ejecutar_conActivoFalso_desactivaElInsumo() {
        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumo));
        when(insumoQuimicoRepositoryPort.guardar(any(InsumoQuimico.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        InsumoQuimicoResult resultado = service.ejecutar(5L, false);

        assertThat(resultado.activo()).isFalse();
    }

    @Test
    void ejecutar_conIdInexistente_lanzaExcepcion() {
        when(insumoQuimicoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L, true))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
