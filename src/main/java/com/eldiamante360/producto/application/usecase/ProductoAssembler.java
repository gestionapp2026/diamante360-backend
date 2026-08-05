package com.eldiamante360.producto.application.usecase;

import com.eldiamante360.producto.application.dto.CategoriaResult;
import com.eldiamante360.producto.application.dto.ProductoResult;
import com.eldiamante360.producto.domain.model.CategoriaProducto;
import com.eldiamante360.producto.domain.model.Producto;

/**
 * Ensambla los DTOs de salida de aplicacion a partir del modelo de dominio.
 * No es un mapper de infraestructura: no conoce JPA ni MapStruct.
 */
final class ProductoAssembler {

    private ProductoAssembler() {
    }

    static CategoriaResult toResult(CategoriaProducto categoria) {
        return new CategoriaResult(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.isActivo()
        );
    }

    static ProductoResult toResult(Producto producto) {
        return new ProductoResult(
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre(),
                producto.getTipoVenta(),
                producto.getUnidadMedida(),
                producto.getPrecioCompra(),
                producto.getPrecioVenta(),
                producto.getStockActual(),
                producto.getStockMinimo(),
                producto.tieneStockBajo(),
                producto.isActivo()
        );
    }
}
