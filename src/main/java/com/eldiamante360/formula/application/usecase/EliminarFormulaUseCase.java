package com.eldiamante360.formula.application.usecase;

public interface EliminarFormulaUseCase {

    /**
     * @param cascada se acepta por consistencia con los demas modulos pero
     *                se ignora: la formula nunca ha tenido dependencias que
     *                bloqueen su borrado.
     */
    void ejecutar(Long id, boolean cascada);
}
