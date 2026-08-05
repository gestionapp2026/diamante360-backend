package com.eldiamante360.orden.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CrearOrdenRequest(
        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "La fecha de entrega es obligatoria")
        @FutureOrPresent(message = "La fecha de entrega debe ser hoy o una fecha futura")
        LocalDate fechaEntrega,

        @Size(max = 1000, message = "Las observaciones no pueden superar los 1000 caracteres")
        String observaciones,

        @NotEmpty(message = "La orden debe tener al menos un detalle")
        List<@Valid DetalleOrdenRequest> detalles
) {
}
