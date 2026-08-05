-- ============================================================================
-- V7: Modulo de deudores (cartera de credito). Cada factura con tipo_pago
-- CREDITO genera automaticamente una cuenta por cobrar (una relacion 1 a 1
-- con factura). Los abonos se registran como lineas independientes y el
-- historial deja la traza de cada evento (creacion, abono, pago total,
-- anulacion).
-- ============================================================================

CREATE TABLE cuenta_por_cobrar (
    id                       BIGSERIAL PRIMARY KEY,
    factura_id               BIGINT         NOT NULL REFERENCES factura (id),
    numero_factura           VARCHAR(20)    NOT NULL,
    cliente_id               BIGINT         NOT NULL REFERENCES cliente (id),
    cliente_nombre           VARCHAR(150)   NOT NULL,
    cliente_numero_documento VARCHAR(20)    NOT NULL,
    monto_original           NUMERIC(14,2)  NOT NULL,
    saldo_pendiente          NUMERIC(14,2)  NOT NULL,
    estado                   VARCHAR(20)    NOT NULL,
    usuario_id               BIGINT         NOT NULL REFERENCES usuario (id),
    fecha                    TIMESTAMPTZ    NOT NULL DEFAULT now(),
    fecha_ultimo_abono       TIMESTAMPTZ,
    fecha_anulacion          TIMESTAMPTZ,
    version                  INTEGER        NOT NULL DEFAULT 0,
    CONSTRAINT uq_cuenta_por_cobrar_factura UNIQUE (factura_id),
    CONSTRAINT ck_cuenta_por_cobrar_estado CHECK (estado IN ('PENDIENTE', 'PARCIAL', 'PAGADA', 'ANULADA')),
    CONSTRAINT ck_cuenta_por_cobrar_monto_original CHECK (monto_original >= 0),
    CONSTRAINT ck_cuenta_por_cobrar_saldo_pendiente CHECK (saldo_pendiente >= 0)
);

CREATE INDEX idx_cuenta_por_cobrar_cliente ON cuenta_por_cobrar (cliente_id, fecha DESC);
CREATE INDEX idx_cuenta_por_cobrar_estado ON cuenta_por_cobrar (estado);

CREATE TABLE abono (
    id                     BIGSERIAL PRIMARY KEY,
    cuenta_por_cobrar_id   BIGINT         NOT NULL REFERENCES cuenta_por_cobrar (id),
    monto                  NUMERIC(14,2)  NOT NULL,
    usuario_id             BIGINT         NOT NULL REFERENCES usuario (id),
    fecha                  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT ck_abono_monto CHECK (monto > 0)
);

CREATE INDEX idx_abono_cuenta ON abono (cuenta_por_cobrar_id, fecha DESC);

CREATE TABLE historial_cuenta_por_cobrar (
    id                    BIGSERIAL PRIMARY KEY,
    cuenta_por_cobrar_id  BIGINT        NOT NULL REFERENCES cuenta_por_cobrar (id),
    tipo_evento           VARCHAR(30)   NOT NULL,
    descripcion           VARCHAR(300)  NOT NULL,
    usuario_id            BIGINT        NOT NULL REFERENCES usuario (id),
    created_at            TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT ck_historial_cxc_tipo_evento CHECK (tipo_evento IN ('CREACION', 'ABONO', 'PAGO_TOTAL', 'ANULACION'))
);

CREATE INDEX idx_historial_cxc_cuenta ON historial_cuenta_por_cobrar (cuenta_por_cobrar_id, created_at DESC);
