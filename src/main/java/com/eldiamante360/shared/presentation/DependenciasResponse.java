package com.eldiamante360.shared.presentation;

import java.util.List;

/**
 * Preview de dependencias de un recurso antes de intentar eliminarlo.
 * Permite al frontend avisar al usuario cuantos registros dependientes
 * existen y si el borrado quedara bloqueado antes de siquiera intentar el
 * {@code DELETE}.
 */
public record DependenciasResponse(
        boolean tieneDependencias,
        boolean bloqueado,
        String mensajeBloqueo,
        List<ConteoDependencia> dependencias
) {
    public record ConteoDependencia(String tipo, String etiqueta, long cantidad) {
    }
}
