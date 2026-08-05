package com.eldiamante360.insumoquimico.domain.exception;

import com.eldiamante360.shared.domain.exception.RecursoDuplicadoException;

public class NombreInsumoQuimicoDuplicadoException extends RecursoDuplicadoException {

    public NombreInsumoQuimicoDuplicadoException(String nombre) {
        super("Ya existe un insumo quimico con el nombre '%s'".formatted(nombre));
    }
}
