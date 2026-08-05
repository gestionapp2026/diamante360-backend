package com.eldiamante360.cliente.presentation.dto.request;

/**
 * rutaId nulo significa "quitar la ruta asignada" al cliente, por lo que
 * deliberadamente no lleva @NotNull.
 */
public record AsignarRutaRequest(
        Long rutaId
) {
}
