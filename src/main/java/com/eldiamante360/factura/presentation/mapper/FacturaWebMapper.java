package com.eldiamante360.factura.presentation.mapper;

import com.eldiamante360.factura.application.dto.CrearFacturaCommand;
import com.eldiamante360.factura.application.dto.FacturaResult;
import com.eldiamante360.factura.presentation.dto.request.CrearFacturaRequest;
import com.eldiamante360.factura.presentation.dto.response.FacturaResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacturaWebMapper {

    CrearFacturaCommand toCommand(CrearFacturaRequest request, Long usuarioId);

    FacturaResponse toResponse(FacturaResult result);

    /**
     * Devuelve una copia de {@code original} con el numero de documento
     * (cedula/RUC) del cliente enmascarado a {@code null}. Usado cuando el
     * usuario autenticado no tiene el permiso {@code CLIENTE_VER_DOCUMENTO}.
     */
    default FacturaResponse enmascararDocumento(FacturaResponse original) {
        return new FacturaResponse(
                original.id(),
                original.numero(),
                original.clienteId(),
                original.clienteNombre(),
                null,
                original.tipoPago(),
                original.detalles(),
                original.estado(),
                original.subtotal(),
                original.descuento(),
                original.total(),
                original.usuarioId(),
                original.usuarioNombre(),
                original.fecha(),
                original.fechaAnulacion(),
                original.medioPago());
    }
}
