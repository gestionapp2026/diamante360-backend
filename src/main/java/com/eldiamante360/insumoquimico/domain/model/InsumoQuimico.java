package com.eldiamante360.insumoquimico.domain.model;

import com.eldiamante360.insumoquimico.domain.exception.StockInsumoInsuficienteException;

import java.math.BigDecimal;

/**
 * Insumo quimico del catalogo (materia prima usada en el proceso productivo,
 * ej. sal de cura, conservantes). El stock agregado se mantiene en esta clase
 * como total denormalizado; el detalle por lote (con vencimiento) vive en
 * {@link LoteInsumo}. El stock solo se mueve a traves de los movimientos de
 * inventario de insumos (kardex), nunca se asigna directamente desde fuera.
 */
public class InsumoQuimico {

    private final Long id;
    private String nombre;
    private final UnidadMedidaInsumo unidadMedida;
    private BigDecimal stockActual;
    private boolean activo;
    private final Integer version;
    private BigDecimal precioCompra;

    public InsumoQuimico(Long id, String nombre, UnidadMedidaInsumo unidadMedida, BigDecimal stockActual,
                          boolean activo, Integer version, BigDecimal precioCompra) {
        this.id = id;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.stockActual = stockActual;
        this.activo = activo;
        this.version = version;
        this.precioCompra = precioCompra;
    }

    public static InsumoQuimico nuevo(String nombre, UnidadMedidaInsumo unidadMedida) {
        return nuevo(nombre, unidadMedida, null);
    }

    public static InsumoQuimico nuevo(String nombre, UnidadMedidaInsumo unidadMedida, BigDecimal precioCompra) {
        return new InsumoQuimico(null, nombre, unidadMedida, BigDecimal.ZERO, true, null, precioCompra);
    }

    public void registrarEntrada(BigDecimal cantidad) {
        this.stockActual = this.stockActual.add(cantidad);
    }

    public void registrarSalida(BigDecimal cantidad) {
        if (this.stockActual.compareTo(cantidad) < 0) {
            throw new StockInsumoInsuficienteException(this.stockActual, cantidad);
        }
        this.stockActual = this.stockActual.subtract(cantidad);
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void actualizarPrecioCompra(BigDecimal precioCompra) {
        if (precioCompra != null && precioCompra.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio de compra no puede ser negativo");
        }
        this.precioCompra = precioCompra;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public UnidadMedidaInsumo getUnidadMedida() {
        return unidadMedida;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public boolean isActivo() {
        return activo;
    }

    public Integer getVersion() {
        return version;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }
}
