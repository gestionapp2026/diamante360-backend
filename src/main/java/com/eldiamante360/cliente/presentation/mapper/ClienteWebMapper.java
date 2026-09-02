package com.eldiamante360.cliente.presentation.mapper;

import com.eldiamante360.cliente.application.dto.ActualizarClienteCommand;
import com.eldiamante360.cliente.application.dto.AsignarRutaClienteCommand;
import com.eldiamante360.cliente.application.dto.ClienteResult;
import com.eldiamante360.cliente.application.dto.CrearClienteCommand;
import com.eldiamante360.cliente.presentation.dto.request.ActualizarClienteRequest;
import com.eldiamante360.cliente.presentation.dto.request.AsignarRutaRequest;
import com.eldiamante360.cliente.presentation.dto.request.CrearClienteRequest;
import com.eldiamante360.cliente.presentation.dto.response.ClienteResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteWebMapper {

    CrearClienteCommand toCommand(CrearClienteRequest request, Long usuarioId);

    ActualizarClienteCommand toCommand(Long clienteId, ActualizarClienteRequest request, Long usuarioId);

    AsignarRutaClienteCommand toAsignarRutaCommand(Long clienteId, AsignarRutaRequest request, Long usuarioId);

    ClienteResponse toResponse(ClienteResult result);

    /**
     * Devuelve una copia de {@code original} con el numero de documento
     * (cedula/RUC) enmascarado a {@code null}. Usado cuando el usuario
     * autenticado no tiene el permiso {@code CLIENTE_VER_DOCUMENTO}.
     */
    default ClienteResponse enmascararDocumento(ClienteResponse original) {
        return new ClienteResponse(
                original.id(),
                original.tipoDocumento(),
                null,
                original.nombre(),
                original.telefonos(),
                original.email(),
                original.direccion(),
                original.rutaId(),
                original.rutaNombre(),
                original.activo());
    }
}
