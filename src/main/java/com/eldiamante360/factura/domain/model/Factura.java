package com.eldiamante360.factura.domain.model;

import com.eldiamante360.factura.domain.exception.DetalleFacturaVacioException;
import com.eldiamante360.factura.domain.exception.FacturaYaAnuladaException;
import com.eldiamante360.shared.domain.model.MedioPago;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Factura de venta. El numero es consecutivo (asignado por el adapter a
 * partir de una secuencia de base de datos), el nombre/documento del
 * cliente se congelan como snapshot al emitir y el subtotal/descuento/total
 * se derivan siempre de la suma de las lineas, nunca se reciben calculados
 * desde afuera. No representa facturacion electronica DIAN, es un
 * comprobante interno.
 */
public class Factura {

    private final Long id;
    private final String numero;
    private final Long clienteId;
    private final String clienteNombre;
    private final String clienteNumeroDocumento;
    private final TipoPago tipoPago;
    private final List<DetalleFactura> detalles;
    private EstadoFactura estado;
    private final BigDecimal subtotal;
    private final BigDecimal descuento;
    private final BigDecimal total;
    private final Long usuarioId;
    private final String usuarioNombre;
    private final Instant fecha;
    private Instant fechaAnulacion;
    private final Integer version;
    private final MedioPago medioPago;

    public Factura(Long id, String numero, Long clienteId, String clienteNombre, String clienteNumeroDocumento,
                    TipoPago tipoPago, List<DetalleFactura> detalles, EstadoFactura estado, BigDecimal subtotal,
                    BigDecimal descuento, BigDecimal total, Long usuarioId, String usuarioNombre, Instant fecha,
                    Instant fechaAnulacion, Integer version, MedioPago medioPago) {
        this.id = id;
        this.numero = numero;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.clienteNumeroDocumento = clienteNumeroDocumento;
        this.tipoPago = tipoPago;
        this.detalles = List.copyOf(detalles);
        this.estado = estado;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.fecha = fecha;
        this.fechaAnulacion = fechaAnulacion;
        this.version = version;
        this.medioPago = medioPago;
    }

    public static Factura nueva(String numero, Long clienteId, String clienteNombre, String clienteNumeroDocumento,
                                 TipoPago tipoPago, List<DetalleFactura> detalles, Long usuarioId,
                                 String usuarioNombre, MedioPago medioPago) {
        if (detalles == null || detalles.isEmpty()) {
            throw new DetalleFacturaVacioException();
        }

        BigDecimal subtotal = detalles.stream().map(DetalleFactura::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal descuento = detalles.stream().map(DetalleFactura::descuento)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = detalles.stream().map(DetalleFactura::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Factura(null, numero, clienteId, clienteNombre, clienteNumeroDocumento, tipoPago, detalles,
                EstadoFactura.EMITIDA, subtotal, descuento, total, usuarioId, usuarioNombre, Instant.now(), null,
                null, medioPago);
    }

    public void anular() {
        if (this.estado == EstadoFactura.ANULADA) {
            throw new FacturaYaAnuladaException(this.id);
        }
        this.estado = EstadoFactura.ANULADA;
        this.fechaAnulacion = Instant.now();
    }

    public boolean estaAnulada() {
        return this.estado == EstadoFactura.ANULADA;
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

    public String getClienteNumeroDocumento() {
        return clienteNumeroDocumento;
    }

    public TipoPago getTipoPago() {
        return tipoPago;
    }

    public List<DetalleFactura> getDetalles() {
        return detalles;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public Instant getFecha() {
        return fecha;
    }

    public Instant getFechaAnulacion() {
        return fechaAnulacion;
    }

    public Integer getVersion() {
        return version;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }
}
