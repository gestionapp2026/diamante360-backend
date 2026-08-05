package com.eldiamante360.insumoquimico.application.port;

import com.eldiamante360.insumoquimico.domain.model.LoteInsumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoteInsumoRepositoryPort {

    Optional<LoteInsumo> buscarPorId(Long id);

    LoteInsumo guardar(LoteInsumo lote);

    Page<LoteInsumo> listarPorInsumo(Long insumoId, Pageable pageable);

    List<LoteInsumo> listarPorVencer(LocalDate hasta);

    /**
     * Lotes con cantidad disponible (&gt; 0) de un insumo, ordenados FEFO
     * (First-Expired-First-Out): fecha de vencimiento ascendente primero
     * (los lotes sin fecha de vencimiento quedan al final, por defecto de
     * ordenamiento de PostgreSQL para ASC), y fecha de ingreso ascendente
     * como desempate. Usado para consumir automaticamente insumos al
     * producir con una formula.
     */
    List<LoteInsumo> listarDisponiblesFefo(Long insumoId);
}
