package com.eldiamante360.orden.presentation.mapper;

import com.eldiamante360.orden.application.dto.CrearOrdenCommand;
import com.eldiamante360.orden.application.dto.OrdenFiltro;
import com.eldiamante360.orden.application.dto.OrdenResult;
import com.eldiamante360.orden.domain.model.EstadoOrden;
import com.eldiamante360.orden.presentation.dto.request.CrearOrdenRequest;
import com.eldiamante360.orden.presentation.dto.response.OrdenResponse;
import org.mapstruct.Mapper;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface OrdenWebMapper {

    CrearOrdenCommand toCommand(CrearOrdenRequest request, Long usuarioId);

    OrdenResponse toResponse(OrdenResult result);

    default OrdenFiltro toFiltro(Long clienteId, EstadoOrden estado, LocalDate fechaEntregaDesde,
                                  LocalDate fechaEntregaHasta, LocalDate fechaCreacionDesde,
                                  LocalDate fechaCreacionHasta) {
        return new OrdenFiltro(clienteId, estado, fechaEntregaDesde, fechaEntregaHasta, fechaCreacionDesde,
                fechaCreacionHasta);
    }
}
