package com.eldiamante360.formula.presentation;

import com.eldiamante360.auth.application.usecase.ObtenerSesionActualUseCase;
import com.eldiamante360.formula.application.usecase.ActualizarFormulaUseCase;
import com.eldiamante360.formula.application.usecase.CambiarEstadoFormulaUseCase;
import com.eldiamante360.formula.application.usecase.CrearFormulaUseCase;
import com.eldiamante360.formula.application.usecase.EliminarFormulaUseCase;
import com.eldiamante360.formula.application.usecase.ListarFormulasUseCase;
import com.eldiamante360.formula.application.usecase.ObtenerDependenciasFormulaUseCase;
import com.eldiamante360.formula.application.usecase.ObtenerFormulaPorProductoUseCase;
import com.eldiamante360.formula.application.usecase.ObtenerFormulaUseCase;
import com.eldiamante360.formula.application.usecase.ProducirConFormulaUseCase;
import com.eldiamante360.formula.presentation.dto.request.ActualizarFormulaRequest;
import com.eldiamante360.formula.presentation.dto.request.CambiarEstadoRequest;
import com.eldiamante360.formula.presentation.dto.request.CrearFormulaRequest;
import com.eldiamante360.formula.presentation.dto.request.ProducirFormulaRequest;
import com.eldiamante360.formula.presentation.dto.response.FormulaResponse;
import com.eldiamante360.formula.presentation.dto.response.ProduccionFormulaResponse;
import com.eldiamante360.formula.presentation.mapper.FormulaWebMapper;
import com.eldiamante360.shared.presentation.DependenciasResponse;
import com.eldiamante360.shared.presentation.PageResponse;
import com.eldiamante360.shared.presentation.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/formulas")
public class FormulaController {

    private final CrearFormulaUseCase crearFormulaUseCase;
    private final ActualizarFormulaUseCase actualizarFormulaUseCase;
    private final CambiarEstadoFormulaUseCase cambiarEstadoFormulaUseCase;
    private final ObtenerFormulaUseCase obtenerFormulaUseCase;
    private final ObtenerFormulaPorProductoUseCase obtenerFormulaPorProductoUseCase;
    private final ListarFormulasUseCase listarFormulasUseCase;
    private final ProducirConFormulaUseCase producirConFormulaUseCase;
    private final EliminarFormulaUseCase eliminarFormulaUseCase;
    private final ObtenerDependenciasFormulaUseCase obtenerDependenciasFormulaUseCase;
    private final ObtenerSesionActualUseCase obtenerSesionActualUseCase;
    private final FormulaWebMapper formulaWebMapper;

    public FormulaController(CrearFormulaUseCase crearFormulaUseCase,
                              ActualizarFormulaUseCase actualizarFormulaUseCase,
                              CambiarEstadoFormulaUseCase cambiarEstadoFormulaUseCase,
                              ObtenerFormulaUseCase obtenerFormulaUseCase,
                              ObtenerFormulaPorProductoUseCase obtenerFormulaPorProductoUseCase,
                              ListarFormulasUseCase listarFormulasUseCase,
                              ProducirConFormulaUseCase producirConFormulaUseCase,
                              EliminarFormulaUseCase eliminarFormulaUseCase,
                              ObtenerDependenciasFormulaUseCase obtenerDependenciasFormulaUseCase,
                              ObtenerSesionActualUseCase obtenerSesionActualUseCase,
                              FormulaWebMapper formulaWebMapper) {
        this.crearFormulaUseCase = crearFormulaUseCase;
        this.actualizarFormulaUseCase = actualizarFormulaUseCase;
        this.cambiarEstadoFormulaUseCase = cambiarEstadoFormulaUseCase;
        this.obtenerFormulaUseCase = obtenerFormulaUseCase;
        this.obtenerFormulaPorProductoUseCase = obtenerFormulaPorProductoUseCase;
        this.listarFormulasUseCase = listarFormulasUseCase;
        this.producirConFormulaUseCase = producirConFormulaUseCase;
        this.eliminarFormulaUseCase = eliminarFormulaUseCase;
        this.obtenerDependenciasFormulaUseCase = obtenerDependenciasFormulaUseCase;
        this.obtenerSesionActualUseCase = obtenerSesionActualUseCase;
        this.formulaWebMapper = formulaWebMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FORMULA_CREAR')")
    public ResponseEntity<FormulaResponse> crear(@Valid @RequestBody CrearFormulaRequest request,
                                                  Authentication authentication) {
        var resultado = crearFormulaUseCase.ejecutar(formulaWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aplicarMascaraSensible(formulaWebMapper.toResponse(resultado), authentication));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FORMULA_EDITAR')")
    public ResponseEntity<FormulaResponse> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ActualizarFormulaRequest request,
                                                        Authentication authentication) {
        var resultado = actualizarFormulaUseCase.ejecutar(formulaWebMapper.toCommand(id, request));
        return ResponseEntity.ok(aplicarMascaraSensible(formulaWebMapper.toResponse(resultado), authentication));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('FORMULA_EDITAR')")
    public ResponseEntity<FormulaResponse> cambiarEstado(@PathVariable Long id,
                                                          @Valid @RequestBody CambiarEstadoRequest request,
                                                          Authentication authentication) {
        var resultado = cambiarEstadoFormulaUseCase.ejecutar(id, request.activo());
        return ResponseEntity.ok(aplicarMascaraSensible(formulaWebMapper.toResponse(resultado), authentication));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('FORMULA_LEER')")
    public ResponseEntity<PageResponse<FormulaResponse>> listar(Pageable pageable, Authentication authentication) {
        var pagina = listarFormulasUseCase.ejecutar(pageable);
        return ResponseEntity.ok(PageResponse.from(pagina,
                resultado -> aplicarMascaraSensible(formulaWebMapper.toResponse(resultado), authentication)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FORMULA_LEER')")
    public ResponseEntity<FormulaResponse> obtener(@PathVariable Long id, Authentication authentication) {
        var resultado = obtenerFormulaUseCase.ejecutar(id);
        return ResponseEntity.ok(aplicarMascaraSensible(formulaWebMapper.toResponse(resultado), authentication));
    }

    @GetMapping("/producto/{productoId}")
    @PreAuthorize("hasAuthority('FORMULA_LEER')")
    public ResponseEntity<FormulaResponse> obtenerPorProducto(@PathVariable Long productoId,
                                                                Authentication authentication) {
        var resultado = obtenerFormulaPorProductoUseCase.ejecutar(productoId);
        return ResponseEntity.ok(aplicarMascaraSensible(formulaWebMapper.toResponse(resultado), authentication));
    }

    @PostMapping("/{id}/producir")
    @PreAuthorize("hasAuthority('FORMULA_PRODUCIR')")
    public ResponseEntity<ProduccionFormulaResponse> producir(@PathVariable Long id,
                                                                @Valid @RequestBody ProducirFormulaRequest request,
                                                                Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = producirConFormulaUseCase.ejecutar(formulaWebMapper.toCommand(id, request, sesion.id()));
        return ResponseEntity.ok(formulaWebMapper.toResponse(resultado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FORMULA_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          @RequestParam(name = "cascada", defaultValue = "false") boolean cascada) {
        eliminarFormulaUseCase.ejecutar(id, cascada);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dependencias")
    @PreAuthorize("hasAuthority('FORMULA_ELIMINAR')")
    public ResponseEntity<DependenciasResponse> dependencias(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerDependenciasFormulaUseCase.ejecutar(id));
    }

    /**
     * Enmascara el nombre de cada insumo quimico en los detalles de la
     * formula si el usuario autenticado no tiene el permiso
     * {@code INSUMO_VER_NOMBRE} (por ejemplo, el rol PLANTA). No afecta la
     * autorizacion de acceso al endpoint (eso lo controla
     * {@code @PreAuthorize}), solo el contenido del campo sensible en el
     * JSON devuelto. Exactamente el mismo patron que
     * {@code ClienteController.aplicarMascaraSensible}.
     */
    private FormulaResponse aplicarMascaraSensible(FormulaResponse response, Authentication authentication) {
        if (SecurityUtils.tieneAutoridad(authentication, "INSUMO_VER_NOMBRE")) {
            return response;
        }
        return formulaWebMapper.enmascararNombreInsumo(response);
    }
}
