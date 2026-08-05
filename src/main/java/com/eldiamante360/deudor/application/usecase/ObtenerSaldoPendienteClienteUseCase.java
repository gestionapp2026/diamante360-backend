package com.eldiamante360.deudor.application.usecase;

import com.eldiamante360.deudor.application.dto.SaldoClienteResult;

public interface ObtenerSaldoPendienteClienteUseCase {

    SaldoClienteResult ejecutar(Long clienteId);
}
