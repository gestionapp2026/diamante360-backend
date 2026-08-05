package com.eldiamante360.producto.infrastructure.persistence.repository;

import com.eldiamante360.producto.infrastructure.persistence.entity.CategoriaProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaProductoJpaRepository extends JpaRepository<CategoriaProductoEntity, Long> {

    boolean existsByNombre(String nombre);
}
