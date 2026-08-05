package com.eldiamante360.cliente.application.usecase;

public interface EliminarClienteUseCase {

    /**
     * @param cascada si es {@code true}, borra tambien las facturas, ordenes
     *                y cuentas por cobrar del cliente antes de eliminarlo
     *                (todas son propiedad exclusiva del cliente). Si es
     *                {@code false} (default), el comportamiento es el mismo
     *                de siempre: bloquea con 422 si existe alguna.
     */
    void ejecutar(Long id, boolean cascada);
}
