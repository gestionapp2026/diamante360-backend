package com.eldiamante360.cliente.infrastructure.persistence.repository;

import com.eldiamante360.cliente.infrastructure.persistence.entity.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {

    boolean existsByNumeroDocumento(String numeroDocumento);

    Page<ClienteEntity> findByRutaId(Long rutaId, Pageable pageable);

    long countByRutaId(Long rutaId);

    @Query("SELECT c FROM ClienteEntity c WHERE "
            + "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR LOWER(c.numeroDocumento) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<ClienteEntity> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    /**
     * Desasigna la ruta de todos los clientes que la tenian asignada. Se usa
     * al eliminar una {@code Ruta}: los clientes NUNCA se borran, solo
     * quedan sin ruta (la columna {@code ruta_id} es nullable).
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ClienteEntity c SET c.ruta = null WHERE c.ruta.id = :rutaId")
    void desasignarRuta(@Param("rutaId") Long rutaId);
}
