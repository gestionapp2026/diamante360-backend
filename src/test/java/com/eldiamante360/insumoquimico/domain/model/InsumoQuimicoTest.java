package com.eldiamante360.insumoquimico.domain.model;

import com.eldiamante360.insumoquimico.domain.exception.StockInsumoInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InsumoQuimicoTest {

    @Test
    void nuevo_creaInsumoActivoConStockCero() {
        InsumoQuimico insumo = InsumoQuimico.nuevo("Sal de cura", UnidadMedidaInsumo.KG);

        assertThat(insumo.isActivo()).isTrue();
        assertThat(insumo.getId()).isNull();
        assertThat(insumo.getStockActual()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void registrarEntrada_sumaAlStockActual() {
        InsumoQuimico insumo = InsumoQuimico.nuevo("Sal de cura", UnidadMedidaInsumo.KG);

        insumo.registrarEntrada(BigDecimal.TEN);

        assertThat(insumo.getStockActual()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void registrarSalida_conStockSuficiente_restaDelStockActual() {
        InsumoQuimico insumo = InsumoQuimico.nuevo("Sal de cura", UnidadMedidaInsumo.KG);
        insumo.registrarEntrada(BigDecimal.TEN);

        insumo.registrarSalida(BigDecimal.valueOf(4));

        assertThat(insumo.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(6));
    }

    @Test
    void registrarSalida_conStockInsuficiente_lanzaExcepcion() {
        InsumoQuimico insumo = InsumoQuimico.nuevo("Sal de cura", UnidadMedidaInsumo.KG);
        insumo.registrarEntrada(BigDecimal.TEN);

        assertThatThrownBy(() -> insumo.registrarSalida(BigDecimal.valueOf(20)))
                .isInstanceOf(StockInsumoInsuficienteException.class);
        assertThat(insumo.getStockActual()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void activarYDesactivar_cambianElEstado() {
        InsumoQuimico insumo = InsumoQuimico.nuevo("Sal de cura", UnidadMedidaInsumo.KG);

        insumo.desactivar();
        assertThat(insumo.isActivo()).isFalse();

        insumo.activar();
        assertThat(insumo.isActivo()).isTrue();
    }
}
