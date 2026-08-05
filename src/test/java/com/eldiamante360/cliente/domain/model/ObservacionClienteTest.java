package com.eldiamante360.cliente.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionClienteTest {

    @Test
    void nueva_creaObservacionConFechaActual() {
        ObservacionCliente observacion = ObservacionCliente.nueva(5L, "Cliente prefiere entregas en la manana", 1L);

        assertThat(observacion.id()).isNull();
        assertThat(observacion.clienteId()).isEqualTo(5L);
        assertThat(observacion.texto()).isEqualTo("Cliente prefiere entregas en la manana");
        assertThat(observacion.usuarioId()).isEqualTo(1L);
        assertThat(observacion.fecha()).isNotNull();
    }
}
