package com.eldiamante360.deudor.domain.model;

import com.eldiamante360.deudor.domain.exception.CuentaPorCobrarAnuladaException;
import com.eldiamante360.deudor.domain.exception.CuentaPorCobrarYaPagadaException;
import com.eldiamante360.deudor.domain.exception.MontoAbonoExcedeSaldoException;
import com.eldiamante360.deudor.domain.exception.MontoAbonoInvalidoException;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Cuenta por cobrar generada automaticamente cuando se emite una factura con
 * tipo de pago CREDITO (una relacion 1 a 1 con la factura que la origina).
 * El saldo pendiente se reduce con cada abono hasta llegar a cero, momento en
 * el cual la cuenta queda PAGADA. No modela fecha de vencimiento, mora ni
 * cupo de credito: esas reglas no han sido definidas todavia por el cliente.
 */
public class CuentaPorCobrar {

    private final Long id;
    private final Long facturaId;
    private final String numeroFactura;
    private final Long clienteId;
    private final String clienteNombre;
    private final String clienteNumeroDocumento;
    private final BigDecimal montoOriginal;
    private BigDecimal saldoPendiente;
    private EstadoCuentaPorCobrar estado;
    private final Long usuarioId;
    private final Instant fecha;
    private Instant fechaUltimoAbono;
    private Instant fechaAnulacion;
    private final Integer version;

    public CuentaPorCobrar(Long id, Long facturaId, String numeroFactura, Long clienteId, String clienteNombre,
                            String clienteNumeroDocumento, BigDecimal montoOriginal, BigDecimal saldoPendiente,
                            EstadoCuentaPorCobrar estado, Long usuarioId, Instant fecha, Instant fechaUltimoAbono,
                            Instant fechaAnulacion, Integer version) {
        this.id = id;
        this.facturaId = facturaId;
        this.numeroFactura = numeroFactura;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.clienteNumeroDocumento = clienteNumeroDocumento;
        this.montoOriginal = montoOriginal;
        this.saldoPendiente = saldoPendiente;
        this.estado = estado;
        this.usuarioId = usuarioId;
        this.fecha = fecha;
        this.fechaUltimoAbono = fechaUltimoAbono;
        this.fechaAnulacion = fechaAnulacion;
        this.version = version;
    }

    public static CuentaPorCobrar nueva(Long facturaId, String numeroFactura, Long clienteId, String clienteNombre,
                                         String clienteNumeroDocumento, BigDecimal montoOriginal, Long usuarioId) {
        return new CuentaPorCobrar(null, facturaId, numeroFactura, clienteId, clienteNombre, clienteNumeroDocumento,
                montoOriginal, montoOriginal, EstadoCuentaPorCobrar.PENDIENTE, usuarioId, Instant.now(), null, null,
                null);
    }

    /**
     * Aplica un abono, reduciendo el saldo pendiente. Si el saldo llega a
     * cero la cuenta pasa a PAGADA, de lo contrario queda (o permanece) en
     * PARCIAL.
     */
    public void registrarAbono(BigDecimal monto) {
        if (this.estado == EstadoCuentaPorCobrar.ANULADA) {
            throw new CuentaPorCobrarAnuladaException(this.id);
        }
        if (this.estado == EstadoCuentaPorCobrar.PAGADA) {
            throw new CuentaPorCobrarYaPagadaException(this.id);
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MontoAbonoInvalidoException(monto);
        }
        if (monto.compareTo(this.saldoPendiente) > 0) {
            throw new MontoAbonoExcedeSaldoException(monto, this.saldoPendiente);
        }

        this.saldoPendiente = this.saldoPendiente.subtract(monto);
        this.estado = this.saldoPendiente.compareTo(BigDecimal.ZERO) == 0
                ? EstadoCuentaPorCobrar.PAGADA
                : EstadoCuentaPorCobrar.PARCIAL;
        this.fechaUltimoAbono = Instant.now();
    }

    public void anular() {
        if (this.estado == EstadoCuentaPorCobrar.ANULADA) {
            throw new CuentaPorCobrarAnuladaException(this.id);
        }
        this.estado = EstadoCuentaPorCobrar.ANULADA;
        this.fechaAnulacion = Instant.now();
    }

    public boolean estaPagada() {
        return this.estado == EstadoCuentaPorCobrar.PAGADA;
    }

    public boolean estaAnulada() {
        return this.estado == EstadoCuentaPorCobrar.ANULADA;
    }

    public Long getId() {
        return id;
    }

    public Long getFacturaId() {
        return facturaId;
    }

    public String getNumeroFactura() {
        return numeroFactura;
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

    public BigDecimal getMontoOriginal() {
        return montoOriginal;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public EstadoCuentaPorCobrar getEstado() {
        return estado;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Instant getFecha() {
        return fecha;
    }

    public Instant getFechaUltimoAbono() {
        return fechaUltimoAbono;
    }

    public Instant getFechaAnulacion() {
        return fechaAnulacion;
    }

    public Integer getVersion() {
        return version;
    }
}
