package com.eldiamante360.cliente.domain.model;

/**
 * Cliente del catalogo comercial (persona natural o juridica que compra
 * los productos de la empresa). Puede asignarse opcionalmente a una
 * {@link Ruta} de reparto/venta. Cada cambio relevante sobre un cliente se
 * registra como evento en su historial (ver modulo de aplicacion), por lo
 * que esta clase solo modela el estado actual, no su trazabilidad.
 */
public class Cliente {

    private final Long id;
    private final TipoDocumentoCliente tipoDocumento;
    private final String numeroDocumento;
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;
    private Ruta ruta;
    private boolean activo;
    private final Integer version;

    public Cliente(Long id, TipoDocumentoCliente tipoDocumento, String numeroDocumento, String nombre,
                    String telefono, String email, String direccion, Ruta ruta, boolean activo, Integer version) {
        this.id = id;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.ruta = ruta;
        this.activo = activo;
        this.version = version;
    }

    public static Cliente nuevo(TipoDocumentoCliente tipoDocumento, String numeroDocumento, String nombre,
                                 String telefono, String email, String direccion, Ruta ruta) {
        return new Cliente(null, tipoDocumento, numeroDocumento, nombre, telefono, email, direccion, ruta, true, null);
    }

    public void actualizarDatos(String nombre, String telefono, String email, String direccion) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    public void asignarRuta(Ruta ruta) {
        this.ruta = ruta;
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

    public TipoDocumentoCliente getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getDireccion() {
        return direccion;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public boolean isActivo() {
        return activo;
    }

    public Integer getVersion() {
        return version;
    }
}
