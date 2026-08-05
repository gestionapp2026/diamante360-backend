package com.eldiamante360.cliente.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RutaTest {

    @Test
    void nueva_creaRutaActiva() {
        Ruta ruta = Ruta.nueva("Ruta Norte", "Zona norte de la ciudad");

        assertThat(ruta.isActivo()).isTrue();
        assertThat(ruta.getId()).isNull();
    }

    @Test
    void actualizarDatos_cambiaNombreYDescripcion() {
        Ruta ruta = Ruta.nueva("Ruta Norte", "Zona norte de la ciudad");

        ruta.actualizarDatos("Ruta Norte Extendida", "Zona norte y noroccidente");

        assertThat(ruta.getNombre()).isEqualTo("Ruta Norte Extendida");
        assertThat(ruta.getDescripcion()).isEqualTo("Zona norte y noroccidente");
    }

    @Test
    void activarYDesactivar_cambianElEstado() {
        Ruta ruta = Ruta.nueva("Ruta Norte", "Zona norte de la ciudad");

        ruta.desactivar();
        assertThat(ruta.isActivo()).isFalse();

        ruta.activar();
        assertThat(ruta.isActivo()).isTrue();
    }
}
