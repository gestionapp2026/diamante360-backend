package com.eldiamante360.orden.infrastructure.persistence.repository;

import com.eldiamante360.orden.infrastructure.persistence.entity.OrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface OrdenJpaRepository extends JpaRepository<OrdenEntity, Long>,
        JpaSpecificationExecutor<OrdenEntity> {

    boolean existsByClienteId(Long clienteId);

    boolean existsByUsuarioId(Long usuarioId);

    long countByClienteId(Long clienteId);

    long countByUsuarioId(Long usuarioId);

    void deleteByClienteId(Long clienteId);

    void deleteByUsuarioId(Long usuarioId);

    @Query(value = "SELECT nextval('seq_numero_orden')", nativeQuery = true)
    Long siguienteNumeroSecuencia();
}
