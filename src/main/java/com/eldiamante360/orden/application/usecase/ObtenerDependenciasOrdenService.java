package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.orden.application.port.OrdenRepositoryPort;
import com.eldiamante360.orden.domain.model.EstadoOrden;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ObtenerDependenciasOrdenService implements ObtenerDependenciasOrdenUseCase {

    static final String MENSAJE_BLOQUEO_PENDIENTE =
            "No se puede eliminar: la orden esta pendiente. Debe estar despachada o anulada antes de borrarla.";

    private final OrdenRepositoryPort ordenRepositoryPort;

    public ObtenerDependenciasOrdenService(OrdenRepositoryPort ordenRepositoryPort) {
        this.ordenRepositoryPort = ordenRepositoryPort;
    }

    @Override
    public DependenciasResponse ejecutar(Long id) {
        var orden = ordenRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden", id));

        boolean bloqueado = orden.getEstado() == EstadoOrden.PENDIENTE;
        String mensajeBloqueo = bloqueado ? MENSAJE_BLOQUEO_PENDIENTE : null;

        return new DependenciasResponse(false, bloqueado, mensajeBloqueo, List.of());
    }
}
