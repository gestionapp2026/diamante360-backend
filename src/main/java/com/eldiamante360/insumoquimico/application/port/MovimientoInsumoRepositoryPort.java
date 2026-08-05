package com.eldiamante360.insumoquimico.application.port;

import com.eldiamante360.insumoquimico.domain.model.MovimientoInsumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovimientoInsumoRepositoryPort {

    MovimientoInsumo guardar(MovimientoInsumo movimiento);

    Page<MovimientoInsumo> listarPorInsumo(Long insumoId, Pageable pageable);
}
