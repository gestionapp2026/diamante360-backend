package com.eldiamante360.producto.application.usecase;

public interface EliminarProductoUseCase {

    /**
     * @param cascada si es {@code true}, borra tambien el kardex
     *                ({@code movimiento_inventario}) y la formula propia del
     *                producto antes de eliminarlo. Nunca cascadea
     *                {@code detalle_factura}/{@code detalle_orden}: si el
     *                producto tiene lineas de factura u orden, el borrado
     *                sigue bloqueado con o sin {@code cascada}.
     */
    void ejecutar(Long id, boolean cascada);
}
