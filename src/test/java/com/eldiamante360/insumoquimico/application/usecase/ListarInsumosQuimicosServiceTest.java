package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.InsumoQuimicoResult;
import com.eldiamante360.insumoquimico.application.port.InsumoQuimicoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarInsumosQuimicosServiceTest {

    @Mock
    private InsumoQuimicoRepositoryPort insumoQuimicoRepositoryPort;

    private ListarInsumosQuimicosService service;

    @BeforeEach
    void setUp() {
        service = new ListarInsumosQuimicosService(insumoQuimicoRepositoryPort);
    }

    @Test
    void ejecutar_retornaLaPaginaMapeada() {
        InsumoQuimico insumo = new InsumoQuimico(5L, "Sal de cura", UnidadMedidaInsumo.KG, BigDecimal.TEN, true, 0, null);
        Pageable pageable = PageRequest.of(0, 10);
        when(insumoQuimicoRepositoryPort.listar(pageable)).thenReturn(new PageImpl<>(List.of(insumo)));

        var resultado = service.ejecutar(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0)).isInstanceOf(InsumoQuimicoResult.class);
        assertThat(resultado.getContent().get(0).nombre()).isEqualTo("Sal de cura");
    }
}
