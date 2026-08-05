package com.eldiamante360.cliente.domain.model;

/**
 * Ruta de reparto/venta a la que se puede asignar un cliente para
 * organizar las visitas comerciales. Solo agrupa clientes; no tiene
 * reglas de negocio mas alla de su propio ciclo de vida activo/inactivo.
 */
public class Ruta {

    private final Long id;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public Ruta(Long id, String nombre, String descripcion, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public static Ruta nueva(String nombre, String descripcion) {
        return new Ruta(null, nombre, descripcion, true);
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
