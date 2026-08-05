package com.eldiamante360.cliente.application.dto;

import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;

public record ClienteResult(
        Long id,
        TipoDocumentoCliente tipoDocumento,
        String numeroDocumento,
        String nombre,
        String telefono,
        String email,
        String direccion,
        Long rutaId,
        String rutaNombre,
        boolean activo
) {
}
