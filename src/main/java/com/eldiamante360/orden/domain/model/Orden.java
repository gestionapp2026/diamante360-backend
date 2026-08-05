package com.eldiamante360.orden.domain.model;

import com.eldiamante360.orden.domain.exception.DetalleOrdenVacioException;
import com.eldiamante360.orden.domain.exception.FechaEntregaInvalidaException;
import com.eldiamante360.orden.domain.exception.OrdenYaAnuladaException;
import com.eldiamante360.orden.domain.exception.OrdenYaDespachadaException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Orden (pedido anticipado) levantada por un vendedor en campo cuando un
 * cliente pide mercancia para despachar mas adelante. El numero es
 * consecutivo (asignado por el adapter a partir de una secuencia de base de
 * datos) y el nombre del cliente y del usuario se congelan como snapshot al
 * crear. No representa una venta ni afecta inventario: es solo un
 * seguimiento de intencion de venta hasta que, eventualmente, se genere la
 * factura correspondiente (fuera del alcance de este modulo).
 */
public class Orden {

    private final Long id;
    private final String numero;
    private final Long clienteId;
    private final String clienteNombre;
    private final Instant fechaCreacion;
    private final LocalDate fechaEntrega;
    private EstadoOrden estado;
    private final String observaciones;
    private final List<DetalleOrden> detalles;
    private final Long usuarioId;
    private final String usuarioNombre;
    private Instant fechaDespacho;
    private Instant fechaAnulacion;
    private final Integer version;

    public Orden(Long id, String numero, Long clienteId, String clienteNombre, Instant fechaCreacion,
                 LocalDate fechaEntrega, EstadoOrden estado, String observaciones, List<DetalleOrden> detalles,
                 Long usuarioId, String usuarioNombre, Instant fechaDespacho, Instant fechaAnulacion,
                 Integer version) {
        this.id = id;
        this.numero = numero;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.fechaCreacion = fechaCreacion;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
        this.observaciones = observaciones;
        this.detalles = List.copyOf(detalles);
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.fechaDespacho = fechaDespacho;
        this.fechaAnulacion = fechaAnulacion;
        this.version = version;
    }

    public static Orden nueva(String numero, Long clienteId, String clienteNombre, LocalDate fechaEntrega,
                               String observaciones, List<DetalleOrden> detalles, Long usuarioId,
                               String usuarioNombre) {
        if (detalles == null || detalles.isEmpty()) {
            throw new DetalleOrdenVacioException();
        }
        if (fechaEntrega == null || fechaEntrega.isBefore(LocalDate.now())) {
            throw new FechaEntregaInvalidaException();
        }

        return new Orden(null, numero, clienteId, clienteNombre, Instant.now(), fechaEntrega, EstadoOrden.PENDIENTE,
                observaciones, detalles, usuarioId, usuarioNombre, null, null, null);
    }

    public void despachar() {
        if (this.estado == EstadoOrden.DESPACHADA) {
            throw new OrdenYaDespachadaException(this.id);
        }
        if (this.estado == EstadoOrden.ANULADA) {
            throw new OrdenYaAnuladaException(this.id);
        }
        this.estado = EstadoOrden.DESPACHADA;
        this.fechaDespacho = Instant.now();
    }

    public void anular() {
        if (this.estado == EstadoOrden.ANULADA) {
            throw new OrdenYaAnuladaException(this.id);
        }
        if (this.estado == EstadoOrden.DESPACHADA) {
            throw new OrdenYaDespachadaException(this.id);
        }
        this.estado = EstadoOrden.ANULADA;
        this.fechaAnulacion = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public List<DetalleOrden> getDetalles() {
        return detalles;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public Instant getFechaDespacho() {
        return fechaDespacho;
    }

    public Instant getFechaAnulacion() {
        return fechaAnulacion;
    }

    public Integer getVersion() {
        return version;
    }
}
