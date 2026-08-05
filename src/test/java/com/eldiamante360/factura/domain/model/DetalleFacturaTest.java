package com.eldiamante360.factura.domain.model;

import com.eldiamante360.factura.domain.exception.CantidadFacturadaInvalidaException;
import com.eldiamante360.factura.domain.exception.PorcentajeDescuentoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DetalleFacturaTest {

    @Test
    void nuevo_sinDescuento_calculaSubtotalDescuentoYTotal() {
        DetalleFactura detalle = DetalleFactura.nuevo(1L, "Chorizo", BigDecimal.valueOf(2),
                BigDecimal.valueOf(1500), BigDecimal.ZERO);

        assertThat(detalle.subtotal()).isEqualByComparingTo(BigDecimal.valueOf(3000).setScale(2));
        assertThat(detalle.descuento()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2));
        assertThat(detalle.total()).isEqualByComparingTo(BigDecimal.valueOf(3000).setScale(2));
    }

    @Test
    void nuevo_conDescuento_calculaSubtotalDescuentoYTotal() {
        DetalleFactura detalle = DetalleFactura.nuevo(1L, "Chorizo", BigDecimal.valueOf(2),
                BigDecimal.valueOf(1500), BigDecimal.TEN);

        assertThat(detalle.subtotal()).isEqualByComparingTo(BigDecimal.valueOf(3000).setScale(2));
        assertThat(detalle.descuento()).isEqualByComparingTo(BigDecimal.valueOf(300).setScale(2));
        assertThat(detalle.total()).isEqualByComparingTo(BigDecimal.valueOf(2700).setScale(2));
    }

    @Test
    void nuevo_conPorcentajeNulo_asumeCeroYNoLanzaExcepcion() {
        DetalleFactura detalle = DetalleFactura.nuevo(1L, "Chorizo", BigDecimal.valueOf(2),
                BigDecimal.valueOf(1500), null);

        assertThat(detalle.porcentajeDescuento()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(detalle.descuento()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2));
    }

    @Test
    void nuevo_redondeaConHalfUpAEscalaDos() {
        DetalleFactura detalle = DetalleFactura.nuevo(1L, "Costilla", new BigDecimal("1.333"),
                new BigDecimal("1000"), BigDecimal.ZERO);

        // 1.333 * 1000 = 1333.000 -> escala 2 HALF_UP = 1333.00
        assertThat(detalle.subtotal()).isEqualByComparingTo(new BigDecimal("1333.00"));
    }

    @Test
    void nuevo_conCantidadNula_lanzaExcepcion() {
        assertThatThrownBy(() -> DetalleFactura.nuevo(1L, "Chorizo", null,
                BigDecimal.valueOf(1500), BigDecimal.ZERO))
                .isInstanceOf(CantidadFacturadaInvalidaException.class);
    }

    @Test
    void nuevo_conCantidadCero_lanzaExcepcion() {
        assertThatThrownBy(() -> DetalleFactura.nuevo(1L, "Chorizo", BigDecimal.ZERO,
                BigDecimal.valueOf(1500), BigDecimal.ZERO))
                .isInstanceOf(CantidadFacturadaInvalidaException.class);
    }

    @Test
    void nuevo_conCantidadNegativa_lanzaExcepcion() {
        assertThatThrownBy(() -> DetalleFactura.nuevo(1L, "Chorizo", BigDecimal.valueOf(-1),
                BigDecimal.valueOf(1500), BigDecimal.ZERO))
                .isInstanceOf(CantidadFacturadaInvalidaException.class);
    }

    @Test
    void nuevo_conPorcentajeNegativo_lanzaExcepcion() {
        assertThatThrownBy(() -> DetalleFactura.nuevo(1L, "Chorizo", BigDecimal.ONE,
                BigDecimal.valueOf(1500), BigDecimal.valueOf(-1)))
                .isInstanceOf(PorcentajeDescuentoInvalidoException.class);
    }

    @Test
    void nuevo_conPorcentajeMayorACien_lanzaExcepcion() {
        assertThatThrownBy(() -> DetalleFactura.nuevo(1L, "Chorizo", BigDecimal.ONE,
                BigDecimal.valueOf(1500), BigDecimal.valueOf(101)))
                .isInstanceOf(PorcentajeDescuentoInvalidoException.class);
    }

    @Test
    void nuevo_conPorcentajeExactamenteCien_noLanzaExcepcion() {
        DetalleFactura detalle = DetalleFactura.nuevo(1L, "Chorizo", BigDecimal.ONE,
                BigDecimal.valueOf(1500), BigDecimal.valueOf(100));

        assertThat(detalle.total()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2));
    }

    @Test
    void nuevo_asignaIdNuloYConservaLosDatosDeEntrada() {
        DetalleFactura detalle = DetalleFactura.nuevo(1L, "Chorizo", BigDecimal.valueOf(2),
                BigDecimal.valueOf(1500), BigDecimal.TEN);

        assertThat(detalle.id()).isNull();
        assertThat(detalle.productoId()).isEqualTo(1L);
        assertThat(detalle.productoNombre()).isEqualTo("Chorizo");
        assertThat(detalle.precioUnitario()).isEqualByComparingTo(BigDecimal.valueOf(1500));
    }
}
