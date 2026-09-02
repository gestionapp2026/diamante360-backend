package com.eldiamante360.cliente.application.dto;

import com.eldiamante360.cliente.domain.model.TipoDocumentoCliente;

import java.util.List;

public record CrearClienteCommand(
        TipoDocumentoCliente tipoDocumento,
        String numeroDocumento,
        String nombre,
        List<String> telefonos,
        String email,
        String direccion,
        Long rutaId,
        Long usuarioId
) {
}
