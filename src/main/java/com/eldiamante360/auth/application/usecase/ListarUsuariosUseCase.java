package com.eldiamante360.auth.application.usecase;

import com.eldiamante360.auth.application.dto.UsuarioResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarUsuariosUseCase {

    Page<UsuarioResult> ejecutar(Pageable pageable);
}
