package com.eldiamante360.formula.application.port;

import com.eldiamante360.formula.domain.model.Formula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FormulaRepositoryPort {

    Optional<Formula> buscarPorId(Long id);

    Optional<Formula> buscarPorProductoId(Long productoId);

    boolean existePorProductoId(Long productoId);

    Formula guardar(Formula formula);

    Page<Formula> listar(Pageable pageable);

    void eliminar(Long id);
}
