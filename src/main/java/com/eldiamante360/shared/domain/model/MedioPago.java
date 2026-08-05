package com.eldiamante360.shared.domain.model;

/**
 * Medio de pago con el que se cancela una factura de contado o un abono a
 * una cuenta por cobrar. Compartido entre los modulos factura y deudor.
 */
public enum MedioPago {
    EFECTIVO,
    NEQUI,
    LLAVE,
    DAVIPLATA,
    BANCOLOMBIA
}
