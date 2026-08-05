package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.factura.infrastructure.persistence.repository.DetalleFacturaJpaRepository;
import com.eldiamante360.formula.infrastructure.persistence.repository.FormulaJpaRepository;
import com.eldiamante360.inventario.infrastructure.persistence.repository.MovimientoInventarioJpaRepository;
import com.eldiamante360.orden.infrastructure.persistence.repository.DetalleOrdenJpaRepository;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.DependenciasResponse.ConteoDependencia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ObtenerDependenciasProductoService implements ObtenerDependenciasProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;
    private final DetalleFacturaJpaRepository detalleFacturaJpaRepository;
    private final DetalleOrdenJpaRepository detalleOrdenJpaRepository;
    private final MovimientoInventarioJpaRepository movimientoInventarioJpaRepository;
    private final FormulaJpaRepository formulaJpaRepository;

    public ObtenerDependenciasProductoService(ProductoRepositoryPort productoRepositoryPort,
                                               DetalleFacturaJpaRepository detalleFacturaJpaRepository,
                                               DetalleOrdenJpaRepository detalleOrdenJpaRepository,
                                               MovimientoInventarioJpaRepository movimientoInventarioJpaRepository,
                                               FormulaJpaRepository formulaJpaRepository) {
        this.productoRepositoryPort = productoRepositoryPort;
        this.detalleFacturaJpaRepository = detalleFacturaJpaRepository;
        this.detalleOrdenJpaRepository = detalleOrdenJpaRepository;
        this.movimientoInventarioJpaRepository = movimientoInventarioJpaRepository;
        this.formulaJpaRepository = formulaJpaRepository;
    }

    @Override
    public DependenciasResponse ejecutar(Long id) {
        var producto = productoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));

        long movimientos = movimientoInventarioJpaRepository.countByProductoId(producto.getId());
        long formula = formulaJpaRepository.existsByProductoId(producto.getId()) ? 1 : 0;
        long facturas = detalleFacturaJpaRepository.countByProductoId(producto.getId());
        long ordenes = detalleOrdenJpaRepository.countByProductoId(producto.getId());

        List<ConteoDependencia> conteos = new ArrayList<>();
        if (movimientos > 0) {
            conteos.add(new ConteoDependencia("movimientosInventario", "Movimientos de inventario", movimientos));
        }
        if (formula > 0) {
            conteos.add(new ConteoDependencia("formula", "Formula", formula));
        }
        if (facturas > 0) {
            conteos.add(new ConteoDependencia("facturas", "Facturas (bloquea)", facturas));
        }
        if (ordenes > 0) {
            conteos.add(new ConteoDependencia("ordenes", "Ordenes (bloquea)", ordenes));
        }

        boolean bloqueado = facturas > 0 || ordenes > 0;
        String mensajeBloqueo = bloqueado ? EliminarProductoService.MENSAJE_BLOQUEO_NO_CASCADEABLE : null;

        boolean tieneDependencias = !conteos.isEmpty();
        return new DependenciasResponse(tieneDependencias, bloqueado, mensajeBloqueo, conteos);
    }
}
