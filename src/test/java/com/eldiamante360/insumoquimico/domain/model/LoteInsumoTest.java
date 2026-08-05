package com.eldiamante360.insumoquimico.domain.model;

import com.eldiamante360.insumoquimico.domain.exception.StockLoteInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoteInsumoTest {

    @Test
    void nuevo_creaLoteConCantidadInicial() {
        LoteInsumo lote = LoteInsumo.nuevo(1L, "L-001", LocalDate.now().plusDays(30), BigDecimal.TEN);

        assertThat(lote.getId()).isNull();
        assertThat(lote.getInsumoId()).isEqualTo(1L);
        assertThat(lote.getCantidadActual()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void nuevo_conNumeroLoteYFechaVencimientoNulos_noLanzaExcepcion() {
        assertThatCode(() -> LoteInsumo.nuevo(1L, null, null, BigDecimal.TEN))
                .doesNotThrowAnyException();
    }

    @Test
    void reducir_conStockSuficiente_restaDeLaCantidadActual() {
        LoteInsumo lote = LoteInsumo.nuevo(1L, "L-001", null, BigDecimal.TEN);

        lote.reducir(BigDecimal.valueOf(4));

        assertThat(lote.getCantidadActual()).isEqualByComparingTo(BigDecimal.valueOf(6));
    }

    @Test
    void reducir_conStockInsuficiente_lanzaExcepcion() {
        LoteInsumo lote = LoteInsumo.nuevo(1L, "L-001", null, BigDecimal.TEN);

        assertThatThrownBy(() -> lote.reducir(BigDecimal.valueOf(20)))
                .isInstanceOf(StockLoteInsuficienteException.class);
        assertThat(lote.getCantidadActual()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void estaVencido_conFechaPasada_retornaTrue() {
        LoteInsumo lote = LoteInsumo.nuevo(1L, "L-001", LocalDate.now().minusDays(1), BigDecimal.TEN);

        assertThat(lote.estaVencido(LocalDate.now())).isTrue();
    }

    @Test
    void estaVencido_conFechaFuturaOSinFecha_retornaFalse() {
        LoteInsumo loteConFecha = LoteInsumo.nuevo(1L, "L-001", LocalDate.now().plusDays(1), BigDecimal.TEN);
        LoteInsumo loteSinFecha = LoteInsumo.nuevo(1L, "L-002", null, BigDecimal.TEN);

        assertThat(loteConFecha.estaVencido(LocalDate.now())).isFalse();
        assertThat(loteSinFecha.estaVencido(LocalDate.now())).isFalse();
    }

    @Test
    void estaPorVencer_dentroDelUmbral_retornaTrue() {
        LoteInsumo lote = LoteInsumo.nuevo(1L, "L-001", LocalDate.now().plusDays(5), BigDecimal.TEN);

        assertThat(lote.estaPorVencer(LocalDate.now(), 10)).isTrue();
    }

    @Test
    void estaPorVencer_fueraDelUmbralOSinFecha_retornaFalse() {
        LoteInsumo loteLejano = LoteInsumo.nuevo(1L, "L-001", LocalDate.now().plusDays(60), BigDecimal.TEN);
        LoteInsumo loteSinFecha = LoteInsumo.nuevo(1L, "L-002", null, BigDecimal.TEN);

        assertThat(loteLejano.estaPorVencer(LocalDate.now(), 10)).isFalse();
        assertThat(loteSinFecha.estaPorVencer(LocalDate.now(), 10)).isFalse();
    }
}
