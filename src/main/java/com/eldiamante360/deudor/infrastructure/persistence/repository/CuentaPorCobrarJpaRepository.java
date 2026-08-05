package com.eldiamante360.deudor.infrastructure.persistence.repository;

import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import com.eldiamante360.deudor.infrastructure.persistence.entity.CuentaPorCobrarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface CuentaPorCobrarJpaRepository extends JpaRepository<CuentaPorCobrarEntity, Long> {

    Optional<CuentaPorCobrarEntity> findByFacturaId(Long facturaId);

    Page<CuentaPorCobrarEntity> findByClienteId(Long clienteId, Pageable pageable);

    Page<CuentaPorCobrarEntity> findByEstado(EstadoCuentaPorCobrar estado, Pageable pageable);

    boolean existsByFacturaId(Long facturaId);

    boolean existsByClienteId(Long clienteId);

    boolean existsByUsuarioId(Long usuarioId);

    long countByFacturaId(Long facturaId);

    long countByClienteId(Long clienteId);

    long countByUsuarioId(Long usuarioId);

    void deleteByFacturaId(Long facturaId);

    void deleteByClienteId(Long clienteId);

    void deleteByUsuarioId(Long usuarioId);

    // cuenta_por_cobrar.factura_id -> factura(id) no tiene ON DELETE CASCADE
    // (ver V7/V18). Para poder cascadear un borrado de usuario hacia las
    // facturas de ese usuario sin violar esa FK, primero hay que borrar las
    // cuentas por cobrar generadas por esas facturas (lo cual a su vez
    // cascadea a abono/historial_cuenta_por_cobrar via V19).
    @Modifying
    @Query("DELETE FROM CuentaPorCobrarEntity c WHERE c.facturaId IN "
            + "(SELECT f.id FROM FacturaEntity f WHERE f.usuarioId = :usuarioId)")
    void deleteByFacturaUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COALESCE(SUM(c.saldoPendiente), 0) FROM CuentaPorCobrarEntity c "
            + "WHERE c.clienteId = :clienteId "
            + "AND c.estado <> com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar.ANULADA")
    BigDecimal sumarSaldoPendientePorCliente(@Param("clienteId") Long clienteId);
}
