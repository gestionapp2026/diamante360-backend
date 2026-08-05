package com.eldiamante360.producto.application.usecase;

public interface EliminarCategoriaUseCase {

    /**
     * @param cascada se acepta por consistencia con los demas modulos pero
     *                se ignora: borrar productos en cascada abriria un
     *                segundo nivel de cascada (facturas/ordenes de esos
     *                productos), demasiado riesgoso. El comportamiento es
     *                siempre el bloqueo actual, con o sin {@code cascada}.
     */
    void ejecutar(Long id, boolean cascada);
}
