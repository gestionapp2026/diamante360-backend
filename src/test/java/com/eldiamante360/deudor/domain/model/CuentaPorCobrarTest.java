package com.eldiamante360.deudor.domain.model;

import com.eldiamante360.deudor.domain.exception.CuentaPorCobrarAnuladaException;
import com.eldiamante360.deudor.domain.exception.CuentaPorCobrarYaPagadaException;
import com.eldiamante360.deudor.domain.exception.MontoAbonoExcedeSaldoException;
import com.eldiamante360.deudor.domain.exception.MontoAbonoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CuentaPorCobrarTest {

    private CuentaPorCobrar cuentaPendiente(BigDecimal montoOriginal) {
        return CuentaPorCobrar.nueva(100L, "FAC-2026-00001", 1L, "Juan Perez", "123456789", montoOriginal, 9L);
    }

    @Test
    void nueva_creaLaCuentaConSaldoPendienteIgualAlMontoOriginalYEstadoPendiente() {
        CuentaPorCobrar cuenta = cuentaPendiente(BigDecimal.valueOf(1000));

        assertThat(cuenta.getId()).isNull();
        assertThat(cuenta.getFacturaId()).isEqualTo(100L);
        assertThat(cuenta.getNumeroFactura()).isEqualTo("FAC-2026-00001");
        assertThat(cuenta.getClienteId()).isEqualTo(1L);
        assertThat(cuenta.getClienteNombre()).isEqualTo("Juan Perez");
        assertThat(cuenta.getClienteNumeroDocumento()).isEqualTo("123456789");
        assertThat(cuenta.getMontoOriginal()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(cuenta.getSaldoPendiente()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(cuenta.getEstado()).isEqualTo(EstadoCuentaPorCobrar.PENDIENTE);
        assertThat(cuenta.getUsuarioId()).isEqualTo(9L);
        assertThat(cuenta.getFecha()).isNotNull();
        assertThat(cuenta.getFechaUltimoAbono()).isNull();
        assertThat(cuenta.getFechaAnulacion()).isNull();
        assertThat(cuenta.getVersion()).isNull();
    }

    @Test
    void registrarAbono_conAbonoParcial_reduceElSaldoYQuedaEnEstadoParcial() {
        CuentaPorCobrar cuenta = cuentaPendiente(BigDecimal.valueOf(1000));

        cuenta.registrarAbono(BigDecimal.valueOf(400));

        assertThat(cuenta.getSaldoPendiente()).isEqualByComparingTo(BigDecimal.valueOf(600));
        assertThat(cuenta.getEstado()).isEqualTo(EstadoCuentaPorCobrar.PARCIAL);
        assertThat(cuenta.getFechaUltimoAbono()).isNotNull();
    }

    @Test
    void registrarAbono_conAbonoQueCubreElSaldoTotal_dejaSaldoEnCeroYEstadoPagada() {
        CuentaPorCobrar cuenta = cuentaPendiente(BigDecimal.valueOf(1000));

        cuenta.registrarAbono(BigDecimal.valueOf(1000));

        assertThat(cuenta.getSaldoPendiente()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(cuenta.getEstado()).isEqualTo(EstadoCuentaPorCobrar.PAGADA);
        assertThat(cuenta.estaPagada()).isTrue();
        assertThat(cuenta.getFechaUltimoAbono()).isNotNull();
    }

    @Test
    void registrarAbono_sobreUnaCuentaAnulada_lanzaExcepcion() {
        CuentaPorCobrar cuenta = cuentaPendiente(BigDecimal.valueOf(1000));
        cuenta.anular();

        assertThatThrownBy(() -> cuenta.registrarAbono(BigDecimal.valueOf(100)))
                .isInstanceOf(CuentaPorCobrarAnuladaException.class);
    }

    @Test
    void registrarAbono_sobreUnaCuentaYaPagada_lanzaExcepcion() {
        CuentaPorCobrar cuenta = cuentaPendiente(BigDecimal.valueOf(1000));
        cuenta.registrarAbono(BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> cuenta.registrarAbono(BigDecimal.valueOf(100)))
                .isInstanceOf(CuentaPorCobrarYaPagadaException.class);
    }

    @Test
    void registrarAbono_conMontoCeroONegativo_lanzaExcepcion() {
        CuentaPorCobrar cuenta = cuentaPendiente(BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> cuenta.registrarAbono(BigDecimal.ZERO))
                .isInstanceOf(MontoAbonoInvalidoException.class);
        assertThatThrownBy(() -> cuenta.registrarAbono(BigDecimal.valueOf(-100)))
                .isInstanceOf(MontoAbonoInvalidoException.class);
    }

    @Test
    void registrarAbono_conMontoMayorAlSaldoPendiente_lanzaExcepcion() {
        CuentaPorCobrar cuenta = cuentaPendiente(BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> cuenta.registrarAbono(BigDecimal.valueOf(1500)))
                .isInstanceOf(MontoAbonoExcedeSaldoException.class);
    }

    @Test
    void anular_conCuentaPendiente_cambiaEstadoYRegistraFechaDeAnulacion() {
        CuentaPorCobrar cuenta = cuentaPendiente(BigDecimal.valueOf(1000));

        cuenta.anular();

        assertThat(cuenta.getEstado()).isEqualTo(EstadoCuentaPorCobrar.ANULADA);
        assertThat(cuenta.estaAnulada()).isTrue();
        assertThat(cuenta.getFechaAnulacion()).isNotNull();
    }

    @Test
    void anular_conCuentaYaAnulada_lanzaExcepcion() {
        CuentaPorCobrar cuenta = cuentaPendiente(BigDecimal.valueOf(1000));
        cuenta.anular();

        assertThatThrownBy(cuenta::anular)
                .isInstanceOf(CuentaPorCobrarAnuladaException.class);
    }
}
