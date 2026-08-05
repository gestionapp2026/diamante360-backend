package com.eldiamante360.orden.application.dto;

import com.eldiamante360.orden.domain.model.EstadoOrden;

import java.time.LocalDate;

/**
 * Combinacion de filtros opcionales para listar ordenes. Todos los campos
 * son nullable: cada uno se aplica solo si viene informado. Se usa un
 * objeto de filtro en vez de un metodo por combinacion porque, a diferencia
 * de otros modulos, aqui hay demasiadas combinaciones simultaneas posibles
 * (cliente, estado, rango de fecha de entrega, rango de fecha de creacion).
 */
public record OrdenFiltro(
        Long clienteId,
        EstadoOrden estado,
        LocalDate fechaEntregaDesde,
        LocalDate fechaEntregaHasta,
        LocalDate fechaCreacionDesde,
        LocalDate fechaCreacionHasta
) {
}
