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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerInsumoQuimicoServiceTest {

    @Mock
    private InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    private ObtenerInsumoQuimicoService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerInsumoQuimicoService(insumoQuimicoRepositoryPort);
    }

    @Test
    void ejecutar_conIdExistente_retornaElInsumo() {
        InsumoQuimico insumo = new InsumoQuimico(5L, "Sal de cura", UnidadMedidaInsumo.KG, BigDecimal.TEN, true, 0, null);
        when(insumoQuimicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(insumo));

        InsumoQuimicoResult resultado = service.ejecutar(5L);

        assertThat(resultado.id()).isEqualTo(5L);
        assertThat(resultado.nombre()).isEqualTo("Sal de cura");
    }

    @Test
    void ejecutar_conIdInexistente_lanzaExcepcion() {
        when(insumoQuimicoRepositoryPort.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(404L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
