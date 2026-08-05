package com.eldiamante360.insumoquimico.application.usecase;

import com.eldiamante360.insumoquimico.application.dto.MovimientoInsumoResult;
import com.eldiamante360.insumoquimico.application.port.MovimientoInsumoRepositoryPort;
import com.eldiamante360.insumoquimico.domain.model.MovimientoInsumo;
import com.eldiamante360.insumoquimico.domain.model.TipoMovimientoInsumo;
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
class ListarMovimientosPorInsumoServiceTest {

    @Mock
    private MovimientoInsumoRepositoryPort movimientoInsumoRepositoryPort;

    private ListarMovimientosPorInsumoService service;

    @BeforeEach
    void setUp() {
        service = new ListarMovimientosPorInsumoService(movimientoInsumoRepositoryPort);
    }

    @Test
    void ejecutar_retornaLaPaginaMapeada() {
        MovimientoInsumo movimiento = MovimientoInsumo.nuevo(5L, 1L, TipoMovimientoInsumo.ENTRADA,
                BigDecimal.TEN, BigDecimal.TEN, "Compra", 1L);
        Pageable pageable = PageRequest.of(0, 10);
        when(movimientoInsumoRepositoryPort.listarPorInsumo(5L, pageable)).thenReturn(new PageImpl<>(List.of(movimiento)));

        var resultado = service.ejecutar(5L, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0)).isInstanceOf(MovimientoInsumoResult.class);
        assertThat(resultado.getContent().get(0).tipoMovimiento()).isEqualTo(TipoMovimientoInsumo.ENTRADA);
    }
}
