package com.eldiamante360.cliente.infrastructure.mapper;

import com.eldiamante360.cliente.domain.model.Cliente;
import com.eldiamante360.cliente.infrastructure.persistence.entity.ClienteEntity;
import org.mapstruct.Mapper;

/**
 * Solo mapea entidad -> dominio. El sentido dominio -> entidad se arma a
 * mano en el adapter porque requiere resolver la referencia a RutaEntity
 * (no se reconstruye la ruta completa solo para persistir la FK).
 */
@Mapper(componentModel = "spring", uses = RutaMapper.class)
public interface ClienteMapper {

    Cliente toDomain(ClienteEntity entity);
}
