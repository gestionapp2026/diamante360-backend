package com.eldiamante360.insumoquimico.domain.exception;

import com.eldiamante360.shared.domain.exception.ReglaNegocioException;

import java.time.LocalDate;

public class LoteVencidoException extends ReglaNegocioException {

    public LoteVencidoException(Long loteId, LocalDate fechaVencimiento) {
        super("El lote %d esta vencido (fecha de vencimiento: %s), no se pueden registrar salidas"
                .formatted(loteId, fechaVencimiento));
    }
}
