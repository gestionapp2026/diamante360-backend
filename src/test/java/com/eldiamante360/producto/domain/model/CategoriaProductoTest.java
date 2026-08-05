package com.eldiamante360.producto.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaProductoTest {

    @Test
    void nueva_creaCategoriaActiva() {
        CategoriaProducto categoria = CategoriaProducto.nueva("Ahumados", "Productos ahumados");

        assertThat(categoria.isActivo()).isTrue();
        assertThat(categoria.getId()).isNull();
    }

    @Test
    void actualizarDatos_cambiaNombreYDescripcion() {
        CategoriaProducto categoria = CategoriaProducto.nueva("Ahumados", "Productos ahumados");

        categoria.actualizarDatos("Ahumados premium", "Linea premium");

        assertThat(categoria.getNombre()).isEqualTo("Ahumados premium");
        assertThat(categoria.getDescripcion()).isEqualTo("Linea premium");
    }

    @Test
    void activarYDesactivar_cambianElEstado() {
        CategoriaProducto categoria = CategoriaProducto.nueva("Ahumados", "Productos ahumados");

        categoria.desactivar();
        assertThat(categoria.isActivo()).isFalse();

        categoria.activar();
        assertThat(categoria.isActivo()).isTrue();
    }
}
