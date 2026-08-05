package com.eldiamante360.factura.infrastructure.persistence.repository;

import com.eldiamante360.factura.infrastructure.persistence.entity.DetalleFacturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repo minimo: los detalles de factura se gestionan siempre a traves del
 * agregado {@code FacturaEntity} (cascade), este repositorio solo existe
 * para poder validar si un producto tiene lineas de factura asociadas antes
 * de permitir su eliminacion definitiva.
 */
public interface DetalleFacturaJpaRepository extends JpaRepository<DetalleFacturaEntity, Long> {

    boolean existsByProductoId(Long productoId);

    long countByProductoId(Long productoId);
}
