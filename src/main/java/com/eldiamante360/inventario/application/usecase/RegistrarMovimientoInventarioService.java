package com.eldiamante360.inventario.application.usecase;

import com.eldiamante360.inventario.application.dto.MovimientoResult;
import com.eldiamante360.inventario.application.dto.RegistrarMovimientoCommand;
import com.eldiamante360.inventario.application.port.MovimientoInventarioRepositoryPort;
import com.eldiamante360.inventario.domain.model.MovimientoInventario;
import com.eldiamante360.producto.application.port.ProductoRepositoryPort;
import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegistrarMovimientoInventarioService implements RegistrarMovimientoInventarioUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;
    private final MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort;

    public RegistrarMovimientoInventarioService(ProductoRepositoryPort productoRepositoryPort,
                                                 MovimientoInventarioRepositoryPort movimientoInventarioRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
        this.movimientoInventarioRepositoryPort = movimientoInventarioRepositoryPort;
    }

    @Override
    public MovimientoResult ejecutar(RegistrarMovimientoCommand command) {
        Producto producto = productoRepositoryPort.buscarPorId(command.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", command.productoId()));

        switch (command.tipoMovimiento()) {
            case ENTRADA -> producto.registrarEntrada(command.cantidad());
            case SALIDA -> producto.registrarSalida(command.cantidad());
            case AJUSTE -> producto.ajustarStock(command.cantidad());
        }

        Producto productoActualizado = productoRepositoryPort.guardar(producto);

        MovimientoInventario movimiento = MovimientoInventario.nuevo(
                command.productoId(), command.tipoMovimiento(), command.cantidad(),
                productoActualizado.getStockActual(), command.motivo(), command.usuarioId());

        return MovimientoAssembler.toResult(movimientoInventarioRepositoryPort.guardar(movimiento));
    }
}
