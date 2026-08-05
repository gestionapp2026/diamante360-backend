package com.eldiamante360.deudor.application.port;

import com.eldiamante360.deudor.domain.model.Abono;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AbonoRepositoryPort {

    Abono guardar(Abono abono);

    Page<Abono> listarPorCuenta(Long cuentaPorCobrarId, Pageable pageable);
}
