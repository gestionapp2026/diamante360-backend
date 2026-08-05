package com.eldiamante360.orden.application.port;

import com.eldiamante360.orden.application.dto.OrdenFiltro;
import com.eldiamante360.orden.domain.model.Orden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrdenRepositoryPort {

    Optional<Orden> buscarPorId(Long id);

    Orden guardar(Orden orden);

    Page<Orden> listar(OrdenFiltro filtro, Pageable pageable);

    /**
     * Genera el siguiente numero de orden de forma atomica (secuencia de
     * base de datos), evitando condiciones de carrera entre creaciones
     * concurrentes.
     */
    String siguienteNumero();

    void eliminar(Long id);
}
