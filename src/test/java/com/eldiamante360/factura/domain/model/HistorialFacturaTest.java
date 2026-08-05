package com.eldiamante360.factura.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HistorialFacturaTest {

    @Test
    void nuevo_creaElRegistroConIdNuloYFechaActual() {
        HistorialFactura historial = HistorialFactura.nuevo(5L, TipoEventoFactura.CREACION,
                "Factura FAC-2026-00001 generada", 1L);

        assertThat(historial.id()).isNull();
        assertThat(historial.facturaId()).isEqualTo(5L);
        assertThat(historial.tipoEvento()).isEqualTo(TipoEventoFactura.CREACION);
        assertThat(historial.descripcion()).isEqualTo("Factura FAC-2026-00001 generada");
        assertThat(historial.usuarioId()).isEqualTo(1L);
        assertThat(historial.fecha()).isNotNull();
    }
}
