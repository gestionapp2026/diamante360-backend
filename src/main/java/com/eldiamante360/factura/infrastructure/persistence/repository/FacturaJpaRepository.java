package com.eldiamante360.factura.infrastructure.persistence.repository;

import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.infrastructure.persistence.entity.FacturaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FacturaJpaRepository extends JpaRepository<FacturaEntity, Long> {

    Page<FacturaEntity> findByClienteId(Long clienteId, Pageable pageable);

    Page<FacturaEntity> findByEstado(EstadoFactura estado, Pageable pageable);

    boolean existsByClienteId(Long clienteId);

    boolean existsByUsuarioId(Long usuarioId);

    long countByClienteId(Long clienteId);

    long countByUsuarioId(Long usuarioId);

    void deleteByClienteId(Long clienteId);

    void deleteByUsuarioId(Long usuarioId);

    @Query(value = "SELECT nextval('seq_numero_factura')", nativeQuery = true)
    Long siguienteNumeroSecuencia();
}
