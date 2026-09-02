package com.eldiamante360.cliente.presentation.dto.response;

import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;

import java.util.List;

public record ClienteResponse(
        Long id,
        TipoDocumentoCliente tipoDocumento,
        String numeroDocumento,
        String nombre,
        List<String> telefonos,
        String email,
        String direccion,
        Long rutaId,
        String rutaNombre,
        boolean activo
) {
}
