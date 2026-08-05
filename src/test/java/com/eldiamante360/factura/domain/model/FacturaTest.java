package com.eldiamante360.factura.domain.model;

import com.eldiamante360.factura.domain.exception.DetalleFacturaVacioException;
import com.eldiamante360.factura.domain.exception.FacturaYaAnuladaException;
import com.eldiamante360.shared.domain.model.MedioPago;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FacturaTest {

    private final DetalleFactura detalle1 = DetalleFactura.nuevo(1L, "Chorizo", BigDecimal.valueOf(2),
            BigDecimal.valueOf(1500), BigDecimal.ZERO);
    private final DetalleFactura detalle2 = DetalleFactura.nuevo(2L, "Costilla", BigDecimal.valueOf(3),
            BigDecimal.valueOf(2000), BigDecimal.TEN);

    @Test
    void nueva_conDetalles_calculaSubtotalDescuentoYTotalComoSumaDeLasLineas() {
        Factura factura = Factura.nueva("FAC-2026-00001", 1L, "Juan Perez", "123456789",
                TipoPago.CONTADO, List.of(detalle1, detalle2), 1L, "Ana Gomez", MedioPago.EFECTIVO);

        assertThat(factura.getSubtotal()).isEqualByComparingTo(
                detalle1.subtotal().add(detalle2.subtotal()));
        assertThat(factura.getDescuento()).isEqualByComparingTo(
                detalle1.descuento().add(detalle2.descuento()));
        assertThat(factura.getTotal()).isEqualByComparingTo(
                detalle1.total().add(detalle2.total()));
    }

    @Test
    void nueva_creaFacturaEnEstadoEmitidaSinFechaDeAnulacion() {
        Factura factura = Factura.nueva("FAC-2026-00001", 1L, "Juan Perez", "123456789",
                TipoPago.CONTADO, List.of(detalle1), 1L, "Ana Gomez", MedioPago.EFECTIVO);

        assertThat(factura.getEstado()).isEqualTo(EstadoFactura.EMITIDA);
        assertThat(factura.estaAnulada()).isFalse();
        assertThat(factura.getFechaAnulacion()).isNull();
        assertThat(factura.getId()).isNull();
        assertThat(factura.getVersion()).isNull();
        assertThat(factura.getFecha()).isNotNull();
    }

    @Test
    void nueva_conservaLosDatosDeCabeceraComoSnapshot() {
        Factura factura = Factura.nueva("FAC-2026-00001", 1L, "Juan Perez", "123456789",
                TipoPago.CREDITO, List.of(detalle1), 9L, "Ana Gomez", MedioPago.EFECTIVO);

        assertThat(factura.getNumero()).isEqualTo("FAC-2026-00001");
        assertThat(factura.getClienteId()).isEqualTo(1L);
        assertThat(factura.getClienteNombre()).isEqualTo("Juan Perez");
        assertThat(factura.getClienteNumeroDocumento()).isEqualTo("123456789");
        assertThat(factura.getTipoPago()).isEqualTo(TipoPago.CREDITO);
        assertThat(factura.getUsuarioId()).isEqualTo(9L);
        assertThat(factura.getUsuarioNombre()).isEqualTo("Ana Gomez");
        assertThat(factura.getDetalles()).containsExactly(detalle1);
    }

    @Test
    void nueva_conListaDeDetallesNula_lanzaExcepcion() {
        assertThatThrownBy(() -> Factura.nueva("FAC-2026-00001", 1L, "Juan Perez", "123456789",
                TipoPago.CONTADO, null, 1L, "Ana Gomez", MedioPago.EFECTIVO))
                .isInstanceOf(DetalleFacturaVacioException.class);
    }

    @Test
    void nueva_conListaDeDetallesVacia_lanzaExcepcion() {
        assertThatThrownBy(() -> Factura.nueva("FAC-2026-00001", 1L, "Juan Perez", "123456789",
                TipoPago.CONTADO, List.of(), 1L, "Ana Gomez", MedioPago.EFECTIVO))
                .isInstanceOf(DetalleFacturaVacioException.class);
    }

    @Test
    void anular_conFacturaEmitida_cambiaEstadoYRegistraFechaDeAnulacion() {
        Factura factura = Factura.nueva("FAC-2026-00001", 1L, "Juan Perez", "123456789",
                TipoPago.CONTADO, List.of(detalle1), 1L, "Ana Gomez", MedioPago.EFECTIVO);

        factura.anular();

        assertThat(factura.getEstado()).isEqualTo(EstadoFactura.ANULADA);
        assertThat(factura.estaAnulada()).isTrue();
        assertThat(factura.getFechaAnulacion()).isNotNull();
    }

    @Test
    void anular_conFacturaYaAnulada_lanzaExcepcion() {
        Factura factura = Factura.nueva("FAC-2026-00001", 1L, "Juan Perez", "123456789",
                TipoPago.CONTADO, List.of(detalle1), 1L, "Ana Gomez", MedioPago.EFECTIVO);
        factura.anular();

        assertThatThrownBy(factura::anular)
                .isInstanceOf(FacturaYaAnuladaException.class);
    }
}
