package com.eldiamante360.orden.infrastructure.persistence.specification;

import com.eldiamante360.orden.application.dto.OrdenFiltro;
import com.eldiamante360.orden.infrastructure.persistence.entity.OrdenEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Construye un {@link Specification} combinando, de forma condicional, solo
 * los filtros que vengan informados en {@link OrdenFiltro}. fechaCreacion se
 * almacena como Instant (timestamp con hora) pero se filtra por rango de
 * LocalDate, asi que el rango se traduce al inicio/fin del dia en la zona
 * horaria del servidor.
 */
public final class OrdenSpecifications {

    private OrdenSpecifications() {
    }

    public static Specification<OrdenEntity> conFiltro(OrdenFiltro filtro) {
        return (root, query, cb) -> {
            var predicados = cb.conjunction();

            if (filtro.clienteId() != null) {
                predicados = cb.and(predicados, cb.equal(root.get("clienteId"), filtro.clienteId()));
            }
            if (filtro.estado() != null) {
                predicados = cb.and(predicados, cb.equal(root.get("estado"), filtro.estado()));
            }
            if (filtro.fechaEntregaDesde() != null) {
                predicados = cb.and(predicados,
                        cb.greaterThanOrEqualTo(root.get("fechaEntrega"), filtro.fechaEntregaDesde()));
            }
            if (filtro.fechaEntregaHasta() != null) {
                predicados = cb.and(predicados,
                        cb.lessThanOrEqualTo(root.get("fechaEntrega"), filtro.fechaEntregaHasta()));
            }
            if (filtro.fechaCreacionDesde() != null) {
                predicados = cb.and(predicados, cb.greaterThanOrEqualTo(root.get("fechaCreacion"),
                        inicioDelDia(filtro.fechaCreacionDesde())));
            }
            if (filtro.fechaCreacionHasta() != null) {
                predicados = cb.and(predicados, cb.lessThanOrEqualTo(root.get("fechaCreacion"),
                        finDelDia(filtro.fechaCreacionHasta())));
            }

            return predicados;
        };
    }

    private static java.time.Instant inicioDelDia(LocalDate fecha) {
        return fecha.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    private static java.time.Instant finDelDia(LocalDate fecha) {
        return fecha.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
    }
}
