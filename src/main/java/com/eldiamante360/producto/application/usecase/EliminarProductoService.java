package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.factura.infrastructure.persistence.repository.DetalleFacturaJpaRepository;
import com.eldiamante360.formula.infrastructure.persistence.repository.FormulaJpaRepository;
import com.eldiamante360.inventario.infrastructure.persistence.repository.MovimientoInventarioJpaRepository;
import com.eldiamante360.orden.infrastructure.persistence.repository.DetalleOrdenJpaRepository;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.shared.domain.exception.RecursoConDependenciasException;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarProductoService implements EliminarProductoUseCase {

    static final String MENSAJE_BLOQUEO_NO_CASCADEABLE =
            "No se puede eliminar: el producto tiene facturas u ordenes asociadas "
                    + "(no se puede cascadear porque esos documentos pueden tener otros productos).";

    private final ProductoRepositoryPort productoRepositoryPort;
    private final DetalleFacturaJpaRepository detalleFacturaJpaRepository;
    private final DetalleOrdenJpaRepository detalleOrdenJpaRepository;
    private final MovimientoInventarioJpaRepository movimientoInventarioJpaRepository;
    private final FormulaJpaRepository formulaJpaRepository;

    public EliminarProductoService(ProductoRepositoryPort productoRepositoryPort,
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
    public void ejecutar(Long id, boolean cascada) {
        Producto producto = productoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));

        boolean tieneNoCascadeable = detalleFacturaJpaRepository.existsByProductoId(producto.getId())
                || detalleOrdenJpaRepository.existsByProductoId(producto.getId());

        if (!cascada) {
            boolean tieneDependencias = tieneNoCascadeable
                    || movimientoInventarioJpaRepository.existsByProductoId(producto.getId())
                    || formulaJpaRepository.existsByProductoId(producto.getId());
            if (tieneDependencias) {
                throw new RecursoConDependenciasException(
                        "No se puede eliminar: el producto tiene facturas asociadas.");
            }
        } else {
            // detalle_factura/detalle_orden nunca se cascadean: una
            // factura/orden puede tener otros productos en otras lineas.
            if (tieneNoCascadeable) {
                throw new RecursoConDependenciasException(MENSAJE_BLOQUEO_NO_CASCADEABLE);
            }
            movimientoInventarioJpaRepository.deleteByProductoId(producto.getId());
            formulaJpaRepository.deleteByProductoId(producto.getId());
        }

        productoRepositoryPort.eliminar(producto.getId());
    }
}
