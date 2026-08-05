package com.eldiamante360.cliente.application.usecase;

/**
 * Elimina una ruta de reparto. A diferencia de los demas modulos, la
 * eliminacion de una ruta NUNCA borra sus clientes asignados: siempre los
 * desasigna (columna {@code ruta_id = NULL}) antes de eliminar la ruta,
 * tanto si {@code cascada=true} como si {@code cascada=false}. El parametro
 * {@code cascada} se acepta unicamente por consistencia con el resto de
 * endpoints de eliminacion, pero no cambia el comportamiento.
 */
public interface EliminarRutaUseCase {

    void ejecutar(Long id, boolean cascada);
}
