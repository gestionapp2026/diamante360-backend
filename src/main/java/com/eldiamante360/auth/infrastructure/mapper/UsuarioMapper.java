package com.eldiamante360.auth.infrastructure.mapper;

import com.eldiamante360.auth.domain.model.Usuario;
import com.eldiamante360.auth.infrastructure.persistence.entity.UsuarioEntity;
import org.mapstruct.Mapper;

/**
 * Solo mapea entidad -> dominio. El sentido dominio -> entidad se arma a
 * mano en el adapter porque requiere resolver la referencia a RolEntity
 * (no se reconstruye el rol completo solo para persistir la FK).
 */
@Mapper(componentModel = "spring", uses = RolMapper.class)
public interface UsuarioMapper {

    Usuario toDomain(UsuarioEntity entity);
}
