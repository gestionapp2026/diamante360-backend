package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.LoteResult;
import com.eldiamante360.insumoquimico.application.port.LoteInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.LoteInsumo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarLotesPorVencerServiceTest {

    @Mock
    private LoteInsumoRepositoryPort loteInsumoRepositoryPort;

    private ListarLotesPorVencerService service;

    @BeforeEach
    void setUp() {
        service = new ListarLotesPorVencerService(loteInsumoRepositoryPort);
    }

    @Test
    void ejecutar_retornaLosLotesPorVencerMapeados() {
        LoteInsumo lote = LoteInsumo.nuevo(5L, "L-001", LocalDate.now().plusDays(5), BigDecimal.TEN);
        when(loteInsumoRepositoryPort.listarPorVencer(any(LocalDate.class))).thenReturn(List.of(lote));

        List<LoteResult> resultado = service.ejecutar(10);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isInstanceOf(LoteResult.class);
        assertThat(resultado.get(0).numeroLote()).isEqualTo("L-001");
    }
}
