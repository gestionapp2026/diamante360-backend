package com.eldiamante360.insumoquimico.application.port;

import com.eldiamante360.insumoquimico.domain.model.InsumoQuimico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InsumoQuimicoRepositoryPort {

    Optional<InsumoQuimico> buscarPorId(Long id);

    boolean existePorNombre(String nombre);

    InsumoQuimico guardar(InsumoQuimico insumoQuimico);

    Page<InsumoQuimico> listar(Pageable pageable);

    void eliminar(Long id);
}
