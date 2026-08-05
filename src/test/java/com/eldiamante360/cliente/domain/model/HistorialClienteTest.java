package com.eldiamante360.cliente.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HistorialClienteTest {

    @Test
    void nuevo_creaHistorialConFechaActual() {
        HistorialCliente historial = HistorialCliente.nuevo(5L, TipoEventoCliente.CREACION, "Cliente creado", 1L);

        assertThat(historial.id()).isNull();
        assertThat(historial.clienteId()).isEqualTo(5L);
        assertThat(historial.tipoEvento()).isEqualTo(TipoEventoCliente.CREACION);
        assertThat(historial.descripcion()).isEqualTo("Cliente creado");
        assertThat(historial.usuarioId()).isEqualTo(1L);
        assertThat(historial.fecha()).isNotNull();
    }
}
