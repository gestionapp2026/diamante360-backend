package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.LoteResult;
import com.eldiamante360.insumoquimico.application.port.LoteInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.LoteInsumo;
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
class ListarLotesPorInsumoServiceTest {

    @Mock
    private LoteInsumoRepositoryPort loteInsumoRepositoryPort;

    private ListarLotesPorInsumoService service;

    @BeforeEach
    void setUp() {
        service = new ListarLotesPorInsumoService(loteInsumoRepositoryPort);
    }

    @Test
    void ejecutar_retornaLaPaginaMapeada() {
        LoteInsumo lote = LoteInsumo.nuevo(5L, "L-001", null, BigDecimal.TEN);
        Pageable pageable = PageRequest.of(0, 10);
        when(loteInsumoRepositoryPort.listarPorInsumo(5L, pageable)).thenReturn(new PageImpl<>(List.of(lote)));

        var resultado = service.ejecutar(5L, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0)).isInstanceOf(LoteResult.class);
        assertThat(resultado.getContent().get(0).numeroLote()).isEqualTo("L-001");
    }
}
