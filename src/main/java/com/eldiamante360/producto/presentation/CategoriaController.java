package com.eldiamante360.producto.presentation;

import com.eldiamante360.producto.application.usecase.ActualizarCategoriaUseCase;
import com.eldiamante360.producto.application.usecase.CambiarEstadoCategoriaUseCase;
import com.eldiamante360.producto.application.usecase.CrearCategoriaUseCase;
import com.eldiamante360.producto.application.usecase.EliminarCategoriaUseCase;
import com.eldiamante360.producto.application.usecase.ListarCategoriasUseCase;
import com.eldiamante360.producto.application.usecase.ObtenerDependenciasCategoriaUseCase;
import com.eldiamante360.producto.presentation.dto.request.ActualizarCategoriaRequest;
import com.eldiamante360.producto.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.producto.presentation.dto.request.CrearCategoriaRequest;
import com.eldiamante360.producto.presentation.dto.response.CategoriaResponse;
import com.eldiamante360.producto.presentation.mapper.CategoriaWebMapper;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CrearCategoriaUseCase crearCategoriaUseCase;
    private final ListarCategoriasUseCase listarCategoriasUseCase;
    private final ActualizarCategoriaUseCase actualizarCategoriaUseCase;
    private final CambiarEstadoCategoriaUseCase cambiarEstadoCategoriaUseCase;
    private final EliminarCategoriaUseCase eliminarCategoriaUseCase;
    private final ObtenerDependenciasCategoriaUseCase obtenerDependenciasCategoriaUseCase;
    private final CategoriaWebMapper categoriaWebMapper;

    public CategoriaController(CrearCategoriaUseCase crearCategoriaUseCase,
                                ListarCategoriasUseCase listarCategoriasUseCase,
                                ActualizarCategoriaUseCase actualizarCategoriaUseCase,
                                CambiarEstadoCategoriaUseCase cambiarEstadoCategoriaUseCase,
                                EliminarCategoriaUseCase eliminarCategoriaUseCase,
                                ObtenerDependenciasCategoriaUseCase obtenerDependenciasCategoriaUseCase,
                                CategoriaWebMapper categoriaWebMapper) {
        this.crearCategoriaUseCase = crearCategoriaUseCase;
        this.listarCategoriasUseCase = listarCategoriasUseCase;
        this.actualizarCategoriaUseCase = actualizarCategoriaUseCase;
        this.cambiarEstadoCategoriaUseCase = cambiarEstadoCategoriaUseCase;
        this.eliminarCategoriaUseCase = eliminarCategoriaUseCase;
        this.obtenerDependenciasCategoriaUseCase = obtenerDependenciasCategoriaUseCase;
        this.categoriaWebMapper = categoriaWebMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCTO_CREAR')")
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CrearCategoriaRequest request) {
        var resultado = crearCategoriaUseCase.ejecutar(categoriaWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaWebMapper.toResponse(resultado));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCTO_LEER')")
    public ResponseEntity<List<CategoriaResponse>> listar() {
        var resultado = listarCategoriasUseCase.ejecutar().stream()
                .map(categoriaWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTO_EDITAR')")
    public ResponseEntity<CategoriaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarCategoriaRequest request) {
        var resultado = actualizarCategoriaUseCase.ejecutar(categoriaWebMapper.toCommand(id, request));
        return ResponseEntity.ok(categoriaWebMapper.toResponse(resultado));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('PRODUCTO_ELIMINAR')")
    public ResponseEntity<CategoriaResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoRequest request) {
        var resultado = cambiarEstadoCategoriaUseCase.ejecutar(id, request.activo());
        return ResponseEntity.ok(categoriaWebMapper.toResponse(resultado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTO_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @RequestParam(name = "cascada", defaultValue = "false") boolean cascada) {
        eliminarCategoriaUseCase.ejecutar(id, cascada);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dependencias")
    @PreAuthorize("hasAuthority('PRODUCTO_ELIMINAR')")
    public ResponseEntity<DependenciasResponse> dependencias(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerDependenciasCategoriaUseCase.ejecutar(id));
    }
}
