package com.eldiamante360.producto.infrastructure.persistence.repository;

import com.eldiamante360.producto.infrastructure.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long> {

    boolean existsByNombre(String nombre);

    boolean existsByCategoriaId(Long categoriaId);

    long countByCategoriaId(Long categoriaId);

    @Query("SELECT p FROM ProductoEntity p WHERE p.stockActual <= p.stockMinimo AND p.activo = true")
    List<ProductoEntity> findConStockBajo();
}
