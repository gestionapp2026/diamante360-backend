package com.eldiamante360.orden.application.dto;

import com.eldiamante360.orden.domain.model.EstadoOrden;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OrdenResult(
        Long id,
        String numero,
        Long clienteId,
        String clienteNombre,
        Instant fechaCreacion,
        LocalDate fechaEntrega,
        EstadoOrden estado,
        String observaciones,
        List<DetalleOrdenResult> detalles,
        Long usuarioId,
        String usuarioNombre,
        Instant fechaDespacho,
        Instant fechaAnulacion
) {
}
