package com.eldiamante360.factura.presentation.dto.request;

import com.eldiamante360.factura.domain.model.TipoPago;
import com.eldiamante360.shared.domain.model.MedioPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CrearFacturaRequest(
        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "El tipo de pago es obligatorio")
        TipoPago tipoPago,

        @NotEmpty(message = "La factura debe tener al menos un detalle")
        List<@Valid DetalleFacturaRequest> detalles,

        MedioPago medioPago
) {
}
