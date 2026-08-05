package com.eldiamante360.deudor.application.port;

import com.eldiamante360.deudor.domain.model.HistorialCuentaPorCobrar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistorialCuentaPorCobrarRepositoryPort {

    HistorialCuentaPorCobrar guardar(HistorialCuentaPorCobrar historial);

    Page<HistorialCuentaPorCobrar> listarPorCuenta(Long cuentaPorCobrarId, Pageable pageable);
}
