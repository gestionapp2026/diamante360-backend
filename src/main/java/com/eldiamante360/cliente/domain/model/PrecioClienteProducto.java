package com.eldiamante360.cliente.domain.model;

import java.math.BigDecimal;

/**
 * Precio de venta especial acordado con un cliente para un producto
 * puntual (ej. acuerdos comerciales), que reemplaza al precio de venta
 * estandar del producto al momento de facturar. Si no existe un registro
 * para el par (cliente, producto), se usa el precio de venta normal del
 * producto.
 */
public class PrecioClienteProducto {

    private final Long id;
    private final Long clienteId;
    private final Long productoId;
    private BigDecimal precio;
    private final Integer version;

    public PrecioClienteProducto(Long id, Long clienteId, Long productoId, BigDecimal precio, Integer version) {
        validarPrecio(precio);
        this.id = id;
        this.clienteId = clienteId;
        this.productoId = productoId;
        this.precio = precio;
        this.version = version;
    }

    public static PrecioClienteProducto nuevo(Long clienteId, Long productoId, BigDecimal precio) {
        return new PrecioClienteProducto(null, clienteId, productoId, precio, null);
    }

    public void actualizarPrecio(BigDecimal nuevoPrecio) {
        validarPrecio(nuevoPrecio);
        this.precio = nuevoPrecio;
    }

    private static void validarPrecio(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Integer getVersion() {
        return version;
    }
}
