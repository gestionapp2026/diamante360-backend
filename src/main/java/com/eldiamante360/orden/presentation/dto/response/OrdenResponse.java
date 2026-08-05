package com.eldiamante360.orden.presentation.dto.response;

import com.eldiamante360.orden.domain.model.EstadoOrden;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OrdenResponse(
        Long id,
        String numero,
        Long clienteId,
        String clienteNombre,
        Instant fechaCreacion,
        LocalDate fechaEntrega,
        EstadoOrden estado,
        String observaciones,
        Long usuarioId,
        String usuarioNombre,
        Instant fechaDespacho,
        Instant fechaAnulacion,
        List<DetalleOrdenResponse> detalles
) {
}
