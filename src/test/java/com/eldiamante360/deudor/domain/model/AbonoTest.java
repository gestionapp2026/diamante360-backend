package com.eldiamante360.deudor.domain.model;

import com.eldiamante360.deudor.domain.exception.MedioPagoRequeridoException;
import com.eldiamante360.deudor.domain.exception.MontoAbonoInvalidoException;
import com.eldiamante360.shared.domain.model.MedioPago;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbonoTest {

    @Test
    void nuevo_creaElAbonoConIdNuloYFechaActual() {
        Abono abono = Abono.nuevo(5L, BigDecimal.valueOf(400), 9L, MedioPago.NEQUI);

        assertThat(abono.id()).isNull();
        assertThat(abono.cuentaPorCobrarId()).isEqualTo(5L);
        assertThat(abono.monto()).isEqualByComparingTo(BigDecimal.valueOf(400));
        assertThat(abono.usuarioId()).isEqualTo(9L);
        assertThat(abono.fecha()).isNotNull();
        assertThat(abono.medioPago()).isEqualTo(MedioPago.NEQUI);
    }

    @Test
    void nuevo_conMontoCeroONegativo_lanzaExcepcion() {
        assertThatThrownBy(() -> Abono.nuevo(5L, BigDecimal.ZERO, 9L, MedioPago.EFECTIVO))
                .isInstanceOf(MontoAbonoInvalidoException.class);
        assertThatThrownBy(() -> Abono.nuevo(5L, BigDecimal.valueOf(-100), 9L, MedioPago.EFECTIVO))
                .isInstanceOf(MontoAbonoInvalidoException.class);
    }

    @Test
    void nuevo_conMontoNulo_lanzaExcepcion() {
        assertThatThrownBy(() -> Abono.nuevo(5L, null, 9L, MedioPago.EFECTIVO))
                .isInstanceOf(MontoAbonoInvalidoException.class);
    }

    @Test
    void nuevo_conMedioPagoNulo_lanzaExcepcion() {
        assertThatThrownBy(() -> Abono.nuevo(5L, BigDecimal.valueOf(400), 9L, null))
                .isInstanceOf(MedioPagoRequeridoException.class);
    }
}
