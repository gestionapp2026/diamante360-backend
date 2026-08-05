package com.eldiamante360.producto.domain.model;

import com.eldiamante360.producto.domain.exception.CombinacionTipoVentaInvalidaException;
import com.eldiamante360.producto.domain.exception.StockInsuficienteException;

import java.math.BigDecimal;

/**
 * Producto del catalogo. Soporta los dos modos de venta del negocio:
 * UNIDAD (presentaciones de peso fijo contadas por unidad) y PESO_VARIABLE
 * (el vendedor define el peso exacto al facturar). El stock se mueve
 * exclusivamente a traves de los movimientos de inventario (kardex), nunca
 * se asigna directamente desde fuera de esta clase.
 */
public class Producto {

    private final Long id;
    private String nombre;
    private CategoriaProducto categoria;
    private final TipoVenta tipoVenta;
    private final UnidadMedida unidadMedida;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private boolean activo;
    private final Integer version;

    public Producto(Long id, String nombre, CategoriaProducto categoria, TipoVenta tipoVenta, UnidadMedida unidadMedida,
                     BigDecimal precioCompra, BigDecimal precioVenta, BigDecimal stockActual, BigDecimal stockMinimo,
                     boolean activo, Integer version) {
        validarCombinacionTipoVentaUnidad(tipoVenta, unidadMedida);
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.tipoVenta = tipoVenta;
        this.unidadMedida = unidadMedida;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.activo = activo;
        this.version = version;
    }

    public static Producto nuevo(String nombre, CategoriaProducto categoria, TipoVenta tipoVenta, UnidadMedida unidadMedida,
                                  BigDecimal precioCompra, BigDecimal precioVenta, BigDecimal stockInicial, BigDecimal stockMinimo) {
        return new Producto(null, nombre, categoria, tipoVenta, unidadMedida, precioCompra, precioVenta,
                stockInicial, stockMinimo, true, null);
    }

    private static void validarCombinacionTipoVentaUnidad(TipoVenta tipoVenta, UnidadMedida unidadMedida) {
        if (tipoVenta == TipoVenta.UNIDAD && unidadMedida != UnidadMedida.UND) {
            throw new CombinacionTipoVentaInvalidaException(
                    "Un producto de tipo UNIDAD debe usar unidad de medida UND");
        }
        if (tipoVenta == TipoVenta.PESO_VARIABLE && unidadMedida == UnidadMedida.UND) {
            throw new CombinacionTipoVentaInvalidaException(
                    "Un producto de tipo PESO_VARIABLE debe usar unidad de medida KG o LB");
        }
    }

    public void actualizarDatos(String nombre, CategoriaProducto categoria, BigDecimal precioCompra,
                                 BigDecimal precioVenta, BigDecimal stockMinimo) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockMinimo = stockMinimo;
    }

    public void registrarEntrada(BigDecimal cantidad) {
        this.stockActual = this.stockActual.add(cantidad);
    }

    public void registrarSalida(BigDecimal cantidad) {
        if (this.stockActual.compareTo(cantidad) < 0) {
            throw new StockInsuficienteException(this.stockActual, cantidad);
        }
        this.stockActual = this.stockActual.subtract(cantidad);
    }

    public void ajustarStock(BigDecimal nuevoStock) {
        this.stockActual = nuevoStock;
    }

    public boolean tieneStockBajo() {
        return this.stockActual.compareTo(this.stockMinimo) <= 0;
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

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public TipoVenta getTipoVenta() {
        return tipoVenta;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public boolean isActivo() {
        return activo;
    }

    public Integer getVersion() {
        return version;
    }
}
