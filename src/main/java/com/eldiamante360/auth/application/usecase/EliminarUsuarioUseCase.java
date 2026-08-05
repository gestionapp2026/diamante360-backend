package com.eldiamante360.auth.application.usecase;

public interface EliminarUsuarioUseCase {

    /**
     * @param cascada se acepta por consistencia con los demas modulos pero
     *                se ignora: las dependencias del usuario (facturas,
     *                ordenes, movimientos de kardex) no son de su propiedad
     *                exclusiva, pertenecen tambien a otros clientes/productos,
     *                asi que jamas se cascadean. El comportamiento es siempre
     *                el bloqueo actual, con o sin {@code cascada}.
     */
    void ejecutar(Long id, Long usuarioActualId, boolean cascada);
}
