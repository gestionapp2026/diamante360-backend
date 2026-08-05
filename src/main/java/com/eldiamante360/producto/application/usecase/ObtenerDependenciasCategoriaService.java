package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.port.CategoriaProductoRepositoryPort;
import com.eldiamante360.producto.infrastructure.persistence.repository.ProductoJpaRepository;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.DependenciasResponse.ConteoDependencia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ObtenerDependenciasCategoriaService implements ObtenerDependenciasCategoriaUseCase {

    static final String MENSAJE_BLOQUEO = "No se puede eliminar: existen productos en esta categoria.";

    private final CategoriaProductoRepositoryPort categoriaProductoRepositoryPort;
    private final ProductoJpaRepository productoJpaRepository;

    public ObtenerDependenciasCategoriaService(CategoriaProductoRepositoryPort categoriaProductoRepositoryPort,
                                                ProductoJpaRepository productoJpaRepository) {
        this.categoriaProductoRepositoryPort = categoriaProductoRepositoryPort;
        this.productoJpaRepository = productoJpaRepository;
    }

    @Override
    public DependenciasResponse ejecutar(Long id) {
        var categoria = categoriaProductoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", id));

        long productos = productoJpaRepository.countByCategoriaId(categoria.getId());

        List<ConteoDependencia> conteos = new ArrayList<>();
        if (productos > 0) {
            conteos.add(new ConteoDependencia("productos", "Productos", productos));
        }

        boolean bloqueado = productos > 0;
        String mensajeBloqueo = bloqueado ? MENSAJE_BLOQUEO : null;

        boolean tieneDependencias = !conteos.isEmpty();
        return new DependenciasResponse(tieneDependencias, bloqueado, mensajeBloqueo, conteos);
    }
}
