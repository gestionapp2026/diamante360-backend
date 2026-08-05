package com.eldiamante360.cliente.presentation.dto.response;

import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;

public record ClienteResponse(
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
