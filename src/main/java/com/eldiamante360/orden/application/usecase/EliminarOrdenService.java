package com.eldiamante360.orden.application.usecase;

import com.eldiamante360.orden.application.port.OrdenRepositoryPort;
import com.eldiamante360.orden.domain.model.EstadoOrden;
import com.eldiamante360.orden.domain.model.Orden;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarOrdenService implements EliminarOrdenUseCase {

    private final OrdenRepositoryPort ordenRepositoryPort;

    public EliminarOrdenService(OrdenRepositoryPort ordenRepositoryPort) {
        this.ordenRepositoryPort = ordenRepositoryPort;
    }

    @Override
    public void ejecutar(Long id, boolean cascada) {
        Orden orden = ordenRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden", id));

        if (orden.getEstado() == EstadoOrden.PENDIENTE) {
            throw new RecursoConDependenciasException(
                    "No se puede eliminar: la orden esta pendiente. Debe estar despachada o anulada antes de "
                            + "borrarla.");
        }

        ordenRepositoryPort.eliminar(orden.getId());
    }
}
