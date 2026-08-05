package com.eldiamante360.producto.presentation;

import com.eldiamante360.producto.application.usecase.*;
import com.eldiamante360.producto.presentation.dto.request.ActualizarProductoRequest;
import com.eldiamante360.producto.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.producto.presentation.dto.request.CrearProductoRequest;
import com.eldiamante360.producto.presentation.dto.response.ProductoResponse;
import com.eldiamante360.producto.presentation.mapper.ProductoWebMapper;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final CrearProductoUseCase crearProductoUseCase;
    private final ListarProductosUseCase listarProductosUseCase;
    private final ObtenerProductoUseCase obtenerProductoUseCase;
    private final ActualizarProductoUseCase actualizarProductoUseCase;
    private final CambiarEstadoProductoUseCase cambiarEstadoProductoUseCase;
    private final ListarProductosStockBajoUseCase listarProductosStockBajoUseCase;
    private final EliminarProductoUseCase eliminarProductoUseCase;
    private final ObtenerDependenciasProductoUseCase obtenerDependenciasProductoUseCase;
    private final ProductoWebMapper productoWebMapper;

    public ProductoController(CrearProductoUseCase crearProductoUseCase,
                               ListarProductosUseCase listarProductosUseCase,
                               ObtenerProductoUseCase obtenerProductoUseCase,
                               ActualizarProductoUseCase actualizarProductoUseCase,
                               CambiarEstadoProductoUseCase cambiarEstadoProductoUseCase,
                               ListarProductosStockBajoUseCase listarProductosStockBajoUseCase,
                               EliminarProductoUseCase eliminarProductoUseCase,
                               ObtenerDependenciasProductoUseCase obtenerDependenciasProductoUseCase,
                               ProductoWebMapper productoWebMapper) {
        this.crearProductoUseCase = crearProductoUseCase;
        this.listarProductosUseCase = listarProductosUseCase;
        this.obtenerProductoUseCase = obtenerProductoUseCase;
        this.actualizarProductoUseCase = actualizarProductoUseCase;
        this.cambiarEstadoProductoUseCase = cambiarEstadoProductoUseCase;
        this.listarProductosStockBajoUseCase = listarProductosStockBajoUseCase;
        this.eliminarProductoUseCase = eliminarProductoUseCase;
        this.obtenerDependenciasProductoUseCase = obtenerDependenciasProductoUseCase;
        this.productoWebMapper = productoWebMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCTO_CREAR')")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody CrearProductoRequest request) {
        var resultado = crearProductoUseCase.ejecutar(productoWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(productoWebMapper.toResponse(resultado));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCTO_LEER')")
    public ResponseEntity<PageResponse<ProductoResponse>> listar(Pageable pageable) {
        var pagina = listarProductosUseCase.ejecutar(pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, productoWebMapper::toResponse));
    }

    @GetMapping("/stock-bajo")
    @PreAuthorize("hasAuthority('PRODUCTO_LEER')")
    public ResponseEntity<List<ProductoResponse>> listarConStockBajo() {
        var resultado = listarProductosStockBajoUseCase.ejecutar().stream()
                .map(productoWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTO_LEER')")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        var resultado = obtenerProductoUseCase.ejecutar(id);
        return ResponseEntity.ok(productoWebMapper.toResponse(resultado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTO_EDITAR')")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarProductoRequest request) {
        var resultado = actualizarProductoUseCase.ejecutar(productoWebMapper.toCommand(id, request));
        return ResponseEntity.ok(productoWebMapper.toResponse(resultado));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('PRODUCTO_ELIMINAR')")
    public ResponseEntity<ProductoResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoRequest request) {
        var resultado = cambiarEstadoProductoUseCase.ejecutar(id, request.activo());
        return ResponseEntity.ok(productoWebMapper.toResponse(resultado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTO_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @RequestParam(name = "cascada", defaultValue = "false") boolean cascada) {
        eliminarProductoUseCase.ejecutar(id, cascada);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dependencias")
    @PreAuthorize("hasAuthority('PRODUCTO_ELIMINAR')")
    public ResponseEntity<DependenciasResponse> dependencias(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerDependenciasProductoUseCase.ejecutar(id));
    }
}
