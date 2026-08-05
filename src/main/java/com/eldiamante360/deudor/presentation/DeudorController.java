package com.eldiamante360.deudor.presentation;

import com.eldiamante360.auth.application.usecase.ObtenerSesionActualUseCase;
import com.eldiamante360.deudor.application.usecase.ListarAbonosUseCase;
import com.eldiamante360.deudor.application.usecase.ListarCuentasPorCobrarPorClienteUseCase;
import com.eldiamante360.deudor.application.usecase.ListarCuentasPorCobrarPorEstadoUseCase;
import com.eldiamante360.deudor.application.usecase.ListarCuentasPorCobrarUseCase;
import com.eldiamante360.deudor.application.usecase.ListarHistorialCuentaPorCobrarUseCase;
import com.eldiamante360.deudor.application.usecase.ObtenerCuentaPorCobrarPorFacturaUseCase;
import com.eldiamante360.deudor.application.usecase.ObtenerCuentaPorCobrarUseCase;
import com.eldiamante360.deudor.application.usecase.ObtenerSaldoPendienteClienteUseCase;
import com.eldiamante360.deudor.application.usecase.RegistrarAbonoUseCase;
import com.eldiamante360.deudor.domain.model.EstadoCuentaPorCobrar;
import com.eldiamante360.deudor.presentation.dto.request.RegistrarAbonoRequest;
import com.eldiamante360.deudor.presentation.dto.response.AbonoResponse;
import com.eldiamante360.deudor.presentation.dto.response.CuentaPorCobrarResponse;
import com.eldiamante360.deudor.presentation.dto.response.HistorialCuentaPorCobrarResponse;
import com.eldiamante360.deudor.presentation.dto.response.SaldoClienteResponse;
import com.eldiamante360.deudor.presentation.mapper.AbonoWebMapper;
import com.eldiamante360.deudor.presentation.mapper.CuentaPorCobrarWebMapper;
import com.eldiamante360.deudor.presentation.mapper.HistorialCuentaPorCobrarWebMapper;
import com.eldiamante360.shared.presentation.PageResponse;
import com.eldiamante360.shared.presentation.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Cartera de deudores: consulta de cuentas por cobrar (generadas
 * automaticamente al facturar a credito), registro de abonos, saldo
 * pendiente por cliente e historial de cada cuenta.
 */
@RestController
@RequestMapping("/deudores")
public class DeudorController {

    private final ObtenerCuentaPorCobrarUseCase obtenerCuentaPorCobrarUseCase;
    private final ObtenerCuentaPorCobrarPorFacturaUseCase obtenerCuentaPorCobrarPorFacturaUseCase;
    private final ListarCuentasPorCobrarUseCase listarCuentasPorCobrarUseCase;
    private final ListarCuentasPorCobrarPorClienteUseCase listarCuentasPorCobrarPorClienteUseCase;
    private final ListarCuentasPorCobrarPorEstadoUseCase listarCuentasPorCobrarPorEstadoUseCase;
    private final RegistrarAbonoUseCase registrarAbonoUseCase;
    private final ListarAbonosUseCase listarAbonosUseCase;
    private final ListarHistorialCuentaPorCobrarUseCase listarHistorialCuentaPorCobrarUseCase;
    private final ObtenerSaldoPendienteClienteUseCase obtenerSaldoPendienteClienteUseCase;
    private final ObtenerSesionActualUseCase obtenerSesionActualUseCase;
    private final CuentaPorCobrarWebMapper cuentaPorCobrarWebMapper;
    private final AbonoWebMapper abonoWebMapper;
    private final HistorialCuentaPorCobrarWebMapper historialCuentaPorCobrarWebMapper;

    public DeudorController(ObtenerCuentaPorCobrarUseCase obtenerCuentaPorCobrarUseCase,
                             ObtenerCuentaPorCobrarPorFacturaUseCase obtenerCuentaPorCobrarPorFacturaUseCase,
                             ListarCuentasPorCobrarUseCase listarCuentasPorCobrarUseCase,
                             ListarCuentasPorCobrarPorClienteUseCase listarCuentasPorCobrarPorClienteUseCase,
                             ListarCuentasPorCobrarPorEstadoUseCase listarCuentasPorCobrarPorEstadoUseCase,
                             RegistrarAbonoUseCase registrarAbonoUseCase,
                             ListarAbonosUseCase listarAbonosUseCase,
                             ListarHistorialCuentaPorCobrarUseCase listarHistorialCuentaPorCobrarUseCase,
                             ObtenerSaldoPendienteClienteUseCase obtenerSaldoPendienteClienteUseCase,
                             ObtenerSesionActualUseCase obtenerSesionActualUseCase,
                             CuentaPorCobrarWebMapper cuentaPorCobrarWebMapper,
                             AbonoWebMapper abonoWebMapper,
                             HistorialCuentaPorCobrarWebMapper historialCuentaPorCobrarWebMapper) {
        this.obtenerCuentaPorCobrarUseCase = obtenerCuentaPorCobrarUseCase;
        this.obtenerCuentaPorCobrarPorFacturaUseCase = obtenerCuentaPorCobrarPorFacturaUseCase;
        this.listarCuentasPorCobrarUseCase = listarCuentasPorCobrarUseCase;
        this.listarCuentasPorCobrarPorClienteUseCase = listarCuentasPorCobrarPorClienteUseCase;
        this.listarCuentasPorCobrarPorEstadoUseCase = listarCuentasPorCobrarPorEstadoUseCase;
        this.registrarAbonoUseCase = registrarAbonoUseCase;
        this.listarAbonosUseCase = listarAbonosUseCase;
        this.listarHistorialCuentaPorCobrarUseCase = listarHistorialCuentaPorCobrarUseCase;
        this.obtenerSaldoPendienteClienteUseCase = obtenerSaldoPendienteClienteUseCase;
        this.obtenerSesionActualUseCase = obtenerSesionActualUseCase;
        this.cuentaPorCobrarWebMapper = cuentaPorCobrarWebMapper;
        this.abonoWebMapper = abonoWebMapper;
        this.historialCuentaPorCobrarWebMapper = historialCuentaPorCobrarWebMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DEUDOR_LEER')")
    public ResponseEntity<PageResponse<CuentaPorCobrarResponse>> listar(@RequestParam(required = false) @Nullable Long clienteId,
                                                                         @RequestParam(required = false) @Nullable EstadoCuentaPorCobrar estado,
                                                                         Pageable pageable,
                                                                         Authentication authentication) {
        var pagina = clienteId != null
                ? listarCuentasPorCobrarPorClienteUseCase.ejecutar(clienteId, pageable)
                : estado != null
                    ? listarCuentasPorCobrarPorEstadoUseCase.ejecutar(estado, pageable)
                    : listarCuentasPorCobrarUseCase.ejecutar(pageable);
        return ResponseEntity.ok(PageResponse.from(pagina,
                resultado -> aplicarMascaraSensible(cuentaPorCobrarWebMapper.toResponse(resultado), authentication)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DEUDOR_LEER')")
    public ResponseEntity<CuentaPorCobrarResponse> obtener(@PathVariable Long id, Authentication authentication) {
        var resultado = obtenerCuentaPorCobrarUseCase.ejecutar(id);
        return ResponseEntity.ok(aplicarMascaraSensible(cuentaPorCobrarWebMapper.toResponse(resultado), authentication));
    }

    @GetMapping("/factura/{facturaId}")
    @PreAuthorize("hasAuthority('DEUDOR_LEER')")
    public ResponseEntity<CuentaPorCobrarResponse> obtenerPorFactura(@PathVariable Long facturaId,
                                                                      Authentication authentication) {
        var resultado = obtenerCuentaPorCobrarPorFacturaUseCase.ejecutar(facturaId);
        return ResponseEntity.ok(aplicarMascaraSensible(cuentaPorCobrarWebMapper.toResponse(resultado), authentication));
    }

    @PostMapping("/{id}/abonos")
    @PreAuthorize("hasAuthority('DEUDOR_ABONAR')")
    public ResponseEntity<CuentaPorCobrarResponse> registrarAbono(@PathVariable Long id,
                                                                   @Valid @RequestBody RegistrarAbonoRequest request,
                                                                   Authentication authentication) {
        var sesion = obtenerSesionActualUseCase.ejecutar(authentication.getName());
        var resultado = registrarAbonoUseCase.ejecutar(cuentaPorCobrarWebMapper.toCommand(id, request, sesion.id()));
        return ResponseEntity.ok(aplicarMascaraSensible(cuentaPorCobrarWebMapper.toResponse(resultado), authentication));
    }

    @GetMapping("/{id}/abonos")
    @PreAuthorize("hasAuthority('DEUDOR_LEER')")
    public ResponseEntity<PageResponse<AbonoResponse>> listarAbonos(@PathVariable Long id, Pageable pageable) {
        var pagina = listarAbonosUseCase.ejecutar(id, pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, abonoWebMapper::toResponse));
    }

    @GetMapping("/{id}/historial")
    @PreAuthorize("hasAuthority('DEUDOR_LEER')")
    public ResponseEntity<PageResponse<HistorialCuentaPorCobrarResponse>> listarHistorial(@PathVariable Long id,
                                                                                            Pageable pageable) {
        var pagina = listarHistorialCuentaPorCobrarUseCase.ejecutar(id, pageable);
        return ResponseEntity.ok(PageResponse.from(pagina, historialCuentaPorCobrarWebMapper::toResponse));
    }

    @GetMapping("/clientes/{clienteId}/saldo")
    @PreAuthorize("hasAuthority('DEUDOR_LEER')")
    public ResponseEntity<SaldoClienteResponse> obtenerSaldoPendiente(@PathVariable Long clienteId) {
        var resultado = obtenerSaldoPendienteClienteUseCase.ejecutar(clienteId);
        return ResponseEntity.ok(cuentaPorCobrarWebMapper.toResponse(resultado));
    }

    /**
     * Enmascara el numero de documento (cedula/RUC) del cliente en la
     * respuesta si el usuario autenticado no tiene el permiso
     * {@code CLIENTE_VER_DOCUMENTO}. No afecta la autorizacion de acceso al
     * endpoint (eso lo controla {@code @PreAuthorize}), solo el contenido
     * del campo sensible en el JSON devuelto.
     */
    private CuentaPorCobrarResponse aplicarMascaraSensible(CuentaPorCobrarResponse response, Authentication authentication) {
        if (SecurityUtils.tieneAutoridad(authentication, "CLIENTE_VER_DOCUMENTO")) {
            return response;
        }
        return cuentaPorCobrarWebMapper.enmascararDocumento(response);
    }
}
