package com.eldiamante360.factura.application.port;

import com.eldiamante360.factura.domain.model.EstadoFactura;
import com.eldiamante360.factura.domain.model.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FacturaRepositoryPort {

    Optional<Factura> buscarPorId(Long id);

    Factura guardar(Factura factura);

    Page<Factura> listar(Pageable pageable);

    Page<Factura> listarPorCliente(Long clienteId, Pageable pageable);

    Page<Factura> listarPorEstado(EstadoFactura estado, Pageable pageable);

    /**
     * Genera el siguiente numero de factura de forma atomica (secuencia de
     * base de datos), evitando condiciones de carrera entre facturaciones
     * concurrentes.
     */
    String siguienteNumero();

    void eliminar(Long id);
}
