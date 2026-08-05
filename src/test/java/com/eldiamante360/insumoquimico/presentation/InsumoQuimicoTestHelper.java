package com.eldiamante360.insumoquimico.presentation;

import com.eldiamante360.insumoquimico.domain.model.UnidadMedidaInsumo;
import com.eldiamante360.insumoquimico.presentation.dto.request.CrearInsumoQuimicoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.request.RegistrarEntradaInsumoRequest;
import com.eldiamante360.insumoquimico.presentation.dto.response.InsumoQuimicoResponse;
import com.eldiamante360.insumoquimico.presentation.dto.response.MovimientoInsumoResponse;
import com.eldiamante360.shared.it.AuthTestHelper;
import com.eldiamante360.shared.it.TestDataFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Utilidades para crear Insumos Quimicos y sus movimientos (entradas/salidas)
 * de prueba, via API real, autenticado como ADMIN (unico rol sembrado con
 * INSUMO_CREAR/INSUMO_AJUSTAR; VENDEDOR no tiene ningun permiso INSUMO_*, ver
 * V1__create_seguridad.sql / V3__create_insumo_quimico.sql).
 */
public final class InsumoQuimicoTestHelper {

    private InsumoQuimicoTestHelper() {
    }

    public static InsumoQuimicoResponse crearInsumo(String accessTokenAdmin, UnidadMedidaInsumo unidadMedida) {
        var request = new CrearInsumoQuimicoRequest(TestDataFactory.nombreCompleto("Insumo IT"), unidadMedida);
        return AuthTestHelper.autenticado(accessTokenAdmin)
                .body(request)
                .when().post("/insumos-quimicos")
                .then().statusCode(201)
                .extract().as(InsumoQuimicoResponse.class);
    }

    /** Insumo activo, recien creado, con stock 0 (UnidadMedidaInsumo.UND). */
    public static InsumoQuimicoResponse crearInsumoActivo(String accessTokenAdmin) {
        return crearInsumo(accessTokenAdmin, UnidadMedidaInsumo.UND);
    }

    public static MovimientoInsumoResponse registrarEntrada(String accessTokenAdmin, Long insumoId, String numeroLote,
                                                              LocalDate fechaVencimiento, BigDecimal cantidad, String motivo) {
        var request = new RegistrarEntradaInsumoRequest(numeroLote, fechaVencimiento, cantidad, motivo);
        return AuthTestHelper.autenticado(accessTokenAdmin)
                .body(request)
                .when().post("/insumos-quimicos/{insumoId}/entradas", insumoId)
                .then().statusCode(201)
                .extract().as(MovimientoInsumoResponse.class);
    }

    /** Entrada minima: numero de lote unico generado, sin fecha de vencimiento, motivo generico. */
    public static MovimientoInsumoResponse registrarEntrada(String accessTokenAdmin, Long insumoId, BigDecimal cantidad) {
        return registrarEntrada(accessTokenAdmin, insumoId, "LOTE-" + TestDataFactory.sufijoUnico(), null, cantidad,
                "Entrada de prueba IT");
    }

    public static InsumoQuimicoResponse obtener(String accessTokenAdmin, Long insumoId) {
        return AuthTestHelper.autenticado(accessTokenAdmin)
                .when().get("/insumos-quimicos/{id}", insumoId)
                .then().statusCode(200)
                .extract().as(InsumoQuimicoResponse.class);
    }

    /** Insumo activo + una entrada inicial; devuelve el insumo con el stock ya reflejado (consultado por HTTP). */
    public static InsumoQuimicoResponse crearInsumoConStock(String accessTokenAdmin, BigDecimal cantidadInicial) {
        InsumoQuimicoResponse insumo = crearInsumoActivo(accessTokenAdmin);
        registrarEntrada(accessTokenAdmin, insumo.id(), cantidadInicial);
        return obtener(accessTokenAdmin, insumo.id());
    }

    /** Insumo (con stock actualizado) + el id del lote creado por la entrada, util para pruebas de salida. */
    public static InsumoConLote crearInsumoConLote(String accessTokenAdmin, BigDecimal cantidad) {
        InsumoQuimicoResponse insumo = crearInsumoActivo(accessTokenAdmin);
        MovimientoInsumoResponse entrada = registrarEntrada(accessTokenAdmin, insumo.id(), cantidad);
        return new InsumoConLote(obtener(accessTokenAdmin, insumo.id()), entrada.loteId());
    }

    /**
     * Insumo + lote ya vencido (fechaVencimiento en el pasado). Nota: la
     * entrada NO valida que la fecha de vencimiento sea futura (ver
     * RegistrarEntradaInsumoService), por lo que este seed es posible; la
     * validacion de vencimiento solo ocurre al intentar una SALIDA de ese lote.
     */
    public static InsumoConLote crearInsumoConLoteVencido(String accessTokenAdmin, BigDecimal cantidad) {
        InsumoQuimicoResponse insumo = crearInsumoActivo(accessTokenAdmin);
        MovimientoInsumoResponse entrada = registrarEntrada(accessTokenAdmin, insumo.id(),
                "LOTE-VENCIDO-" + TestDataFactory.sufijoUnico(), LocalDate.now().minusDays(5), cantidad,
                "Entrada de prueba IT con lote ya vencido");
        return new InsumoConLote(obtener(accessTokenAdmin, insumo.id()), entrada.loteId());
    }

    public record InsumoConLote(InsumoQuimicoResponse insumo, Long loteId) {
    }
}
