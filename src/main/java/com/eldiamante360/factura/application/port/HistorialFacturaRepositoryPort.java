package com.eldiamante360.factura.application.port;

import com.eldiamante360.factura.domain.model.HistorialFactura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistorialFacturaRepositoryPort {

    HistorialFactura guardar(HistorialFactura historial);

    Page<HistorialFactura> listarPorFactura(Long facturaId, Pageable pageable);
}
