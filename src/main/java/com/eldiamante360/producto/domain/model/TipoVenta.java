package com.eldiamante360.producto.domain.model;

/**
 * Modo de venta del producto: UNIDAD para presentaciones de peso fijo
 * contadas por unidad (ej. "Costilla ahumada 5 Kg"), PESO_VARIABLE para
 * productos que se venden al peso que el vendedor define al facturar
 * (ej. Chuleta, Pezuna, Lomo).
 */
public enum TipoVenta {
    UNIDAD,
    PESO_VARIABLE
}
