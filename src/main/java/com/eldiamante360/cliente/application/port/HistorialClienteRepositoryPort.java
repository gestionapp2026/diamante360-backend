package com.eldiamante360.cliente.application.port;

import com.eldiamante360.cliente.domain.model.HistorialCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistorialClienteRepositoryPort {

    HistorialCliente guardar(HistorialCliente historial);

    Page<HistorialCliente> listarPorCliente(Long clienteId, Pageable pageable);
}
