package com.eldiamante360.orden.application.usecase;

public interface EliminarOrdenUseCase {

    /**
     * @param cascada se acepta por consistencia con los demas modulos pero
     *                se ignora: la orden ya no tiene ninguna tabla hija que
     *                cascadear, solo la regla de estado (debe estar
     *                ANULADA), que nunca se salta.
     */
    void ejecutar(Long id, boolean cascada);
}
