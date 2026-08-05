package com.eldiamante360.deudor.application.port;

import com.eldiamante360.deudor.domain.model.CuentaPorCobrar;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface CuentaPorCobrarRepositoryPort {

    Optional<CuentaPorCobrar> buscarPorId(Long id);

    Optional<CuentaPorCobrar> buscarPorFacturaId(Long facturaId);

    CuentaPorCobrar guardar(CuentaPorCobrar cuentaPorCobrar);

    Page<CuentaPorCobrar> listar(Pageable pageable);

    Page<CuentaPorCobrar> listarPorCliente(Long clienteId, Pageable pageable);

    Page<CuentaPorCobrar> listarPorEstado(EstadoCuentaPorCobrar estado, Pageable pageable);

    /**
     * Suma el saldo pendiente de todas las cuentas del cliente que no esten
     * ANULADAS. Devuelve cero si no tiene cuentas.
     */
    BigDecimal sumarSaldoPendientePorCliente(Long clienteId);
}
