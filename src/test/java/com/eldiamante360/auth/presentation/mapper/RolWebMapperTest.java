package com.eldiamante360.auth.presentation.mapper;

import com.eldiamante360.auth.domain.model.Permiso;
import com.eldiamante360.auth.domain.model.Rol;
import com.eldiamante360.auth.presentation.dto.response.RolResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RolWebMapperTest {

    private final RolWebMapper mapper = Mappers.getMapper(RolWebMapper.class);

    @Test
    void toResponse_mapeaPermisosComoCodigos() {
        Rol rol = new Rol(1L, "ADMIN", "Administrador", Set.of(
                new Permiso(1L, "USUARIO_CREAR", "Crear usuarios", "USUARIO"),
                new Permiso(2L, "USUARIO_EDITAR", "Editar usuarios", "USUARIO")
        ));

        RolResponse response = mapper.toResponse(rol);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nombre()).isEqualTo("ADMIN");
        assertThat(response.permisos()).containsExactlyInAnyOrder("USUARIO_CREAR", "USUARIO_EDITAR");
    }

    @Test
    void toResponse_conRolSinPermisos_retornaConjuntoVacio() {
        Rol rol = new Rol(2L, "VENDEDOR", "Vendedor", Set.of());

        RolResponse response = mapper.toResponse(rol);

        assertThat(response.permisos()).isEmpty();
    }
}
