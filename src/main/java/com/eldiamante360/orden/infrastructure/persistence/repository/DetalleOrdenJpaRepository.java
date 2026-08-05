package com.eldiamante360.orden.infrastructure.persistence.repository;

import com.eldiamante360.orden.infrastructure.persistence.entity.DetalleOrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repo minimo: los detalles de orden se gestionan siempre a traves del
 * agregado {@code OrdenEntity} (cascade), este repositorio solo existe para
 * poder validar si un producto tiene lineas de orden asociadas antes de
 * permitir su eliminacion definitiva.
 */
public interface DetalleOrdenJpaRepository extends JpaRepository<DetalleOrdenEntity, Long> {

    boolean existsByProductoId(Long productoId);

    long countByProductoId(Long productoId);
}
