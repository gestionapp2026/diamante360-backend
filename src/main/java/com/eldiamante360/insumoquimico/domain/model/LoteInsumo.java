package com.eldiamante360.insumoquimico.domain.model;

import com.eldiamante360.insumoquimico.domain.exception.StockLoteInsuficienteException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Lote de un insumo quimico. El numero de lote y la fecha de vencimiento son
 * opcionales (confirmado por el cliente: no todo insumo se compra por lotes
 * numerados). Cada entrada de inventario crea un lote nuevo; las salidas
 * siempre descuentan de un lote existente especifico, habilitando trazabilidad
 * FEFO y reportes de vencimiento.
 */
public class LoteInsumo {

    private final Long id;
    private final Long insumoId;
    private final String numeroLote;
    private final LocalDate fechaVencimiento;
    private BigDecimal cantidadActual;
    private final Instant fechaIngreso;
    private final Integer version;

    public LoteInsumo(Long id, Long insumoId, String numeroLote, LocalDate fechaVencimiento,
                       BigDecimal cantidadActual, Instant fechaIngreso, Integer version) {
        this.id = id;
        this.insumoId = insumoId;
        this.numeroLote = numeroLote;
        this.fechaVencimiento = fechaVencimiento;
        this.cantidadActual = cantidadActual;
        this.fechaIngreso = fechaIngreso;
        this.version = version;
    }

    public static LoteInsumo nuevo(Long insumoId, String numeroLote, LocalDate fechaVencimiento, BigDecimal cantidadInicial) {
        return new LoteInsumo(null, insumoId, numeroLote, fechaVencimiento, cantidadInicial, Instant.now(), null);
    }

    public void reducir(BigDecimal cantidad) {
        if (this.cantidadActual.compareTo(cantidad) < 0) {
            throw new StockLoteInsuficienteException(this.id, this.cantidadActual, cantidad);
        }
        this.cantidadActual = this.cantidadActual.subtract(cantidad);
    }

    public boolean estaVencido(LocalDate hoy) {
        return this.fechaVencimiento != null && this.fechaVencimiento.isBefore(hoy);
    }

    public boolean estaPorVencer(LocalDate hoy, int diasUmbral) {
        if (this.fechaVencimiento == null) {
            return false;
        }
        LocalDate limite = hoy.plusDays(diasUmbral);
        return !this.fechaVencimiento.isBefore(hoy) && !this.fechaVencimiento.isAfter(limite);
    }

    public Long getId() {
        return id;
    }

    public Long getInsumoId() {
        return insumoId;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public BigDecimal getCantidadActual() {
        return cantidadActual;
    }

    public Instant getFechaIngreso() {
        return fechaIngreso;
    }

    public Integer getVersion() {
        return version;
    }
}
