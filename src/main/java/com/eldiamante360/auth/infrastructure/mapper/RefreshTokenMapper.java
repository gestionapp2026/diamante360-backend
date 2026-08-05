package com.eldiamante360.auth.infrastructure.mapper;

import com.eldiamante360.auth.domain.model.RefreshToken;
import com.eldiamante360.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    RefreshToken toDomain(RefreshTokenEntity entity);

    RefreshTokenEntity toEntity(RefreshToken domain);
}
