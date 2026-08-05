package com.eldiamante360.producto.domain.model;

/**
 * Categoria de clasificacion del catalogo de productos (ej. Ahumados,
 * Naturales, Embutidos). Solo agrupa productos; no tiene reglas de negocio
 * mas alla de su propio ciclo de vida activo/inactivo.
 */
public class CategoriaProducto {

    private final Long id;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public CategoriaProducto(Long id, String nombre, String descripcion, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public static CategoriaProducto nueva(String nombre, String descripcion) {
        return new CategoriaProducto(null, nombre, descripcion, true);
    }

    public void actualizarDatos(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }
}
