package com.eldiamante360.producto.infrastructure.mapper;

import com.eldiamante360.producto.domain.model.Producto;
import com.eldiamante360.producto.infrastructure.persistence.entity.ProductoEntity;
import org.mapstruct.Mapper;

/**
 * Solo mapea entidad -> dominio. El sentido dominio -> entidad se arma a
 * mano en el adapter porque requiere resolver la referencia a
 * CategoriaProductoEntity (no se reconstruye la categoria completa solo
 * para persistir la FK).
 */
@Mapper(componentModel = "spring", uses = CategoriaProductoMapper.class)
public interface ProductoMapper {

    Producto toDomain(ProductoEntity entity);
}
