package com.eldiamante360.deudor.presentation.mapper;

import com.eldiamante360.deudor.application.dto.CuentaPorCobrarResult;
import com.eldiamante360.deudor.application.dto.RegistrarAbonoCommand;
import com.eldiamante360.deudor.application.dto.SaldoClienteResult;
import com.eldiamante360.deudor.presentation.dto.request.RegistrarAbonoRequest;
import com.eldiamante360.deudor.presentation.dto.response.CuentaPorCobrarResponse;
import com.eldiamante360.deudor.presentation.dto.response.SaldoClienteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuentaPorCobrarWebMapper {

    @Mapping(target = "cuentaPorCobrarId", source = "cuentaPorCobrarId")
    RegistrarAbonoCommand toCommand(Long cuentaPorCobrarId, RegistrarAbonoRequest request, Long usuarioId);

    CuentaPorCobrarResponse toResponse(CuentaPorCobrarResult result);

    SaldoClienteResponse toResponse(SaldoClienteResult result);

    /**
     * Devuelve una copia de {@code original} con el numero de documento
     * (cedula/RUC) del cliente enmascarado a {@code null}. Usado cuando el
     * usuario autenticado no tiene el permiso {@code CLIENTE_VER_DOCUMENTO}.
     */
    default CuentaPorCobrarResponse enmascararDocumento(CuentaPorCobrarResponse original) {
        return new CuentaPorCobrarResponse(
                original.id(),
                original.facturaId(),
                original.numeroFactura(),
                original.clienteId(),
                original.clienteNombre(),
                null,
                original.montoOriginal(),
                original.saldoPendiente(),
                original.estado(),
                original.usuarioId(),
                original.fecha(),
                original.fechaUltimoAbono(),
                original.fechaAnulacion());
    }
}
