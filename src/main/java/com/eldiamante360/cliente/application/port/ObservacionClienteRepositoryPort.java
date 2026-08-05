package com.eldiamante360.cliente.application.port;

import com.eldiamante360.cliente.domain.model.ObservacionCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ObservacionClienteRepositoryPort {

    ObservacionCliente guardar(ObservacionCliente observacion);

    Page<ObservacionCliente> listarPorCliente(Long clienteId, Pageable pageable);
}
