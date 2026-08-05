package com.eldiamante360.insumoquimico.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

public class InsumoInactivoException extends ReglaNegocioException {

    public InsumoInactivoException(String nombreInsumo) {
        super("El insumo quimico '%s' esta inactivo, no se pueden registrar movimientos".formatted(nombreInsumo));
    }
}
