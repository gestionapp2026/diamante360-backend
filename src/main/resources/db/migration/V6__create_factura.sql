-- ============================================================================
-- V6: Modulo de facturacion (encabezado, detalle de lineas, historial de
-- eventos). El descuento de inventario se registra a traves del kardex ya
-- existente (movimiento_inventario, V2), no se crea una tabla nueva para eso.
-- ============================================================================

CREATE SEQUENCE seq_numero_factura START WITH 1 INCREMENT BY 1;

CREATE TABLE factura (
    id                       BIGSERIAL PRIMARY KEY,
    numero                   VARCHAR(20)    NOT NULL,
    cliente_id               BIGINT         NOT NULL REFERENCES cliente (id),
    cliente_nombre           VARCHAR(150)   NOT NULL,
    cliente_numero_documento VARCHAR(20)    NOT NULL,
    tipo_pago                VARCHAR(10)    NOT NULL,
    estado                   VARCHAR(20)    NOT NULL,
    subtotal                 NUMERIC(14,2)  NOT NULL,
    descuento                NUMERIC(14,2)  NOT NULL,
    total                    NUMERIC(14,2)  NOT NULL,
    usuario_id               BIGINT         NOT NULL REFERENCES usuario (id),
    fecha                    TIMESTAMPTZ    NOT NULL DEFAULT now(),
    fecha_anulacion          TIMESTAMPTZ,
    version                  INTEGER        NOT NULL DEFAULT 0,
    CONSTRAINT uq_factura_numero UNIQUE (numero),
    CONSTRAINT ck_factura_tipo_pago CHECK (tipo_pago IN ('CONTADO', 'CREDITO')),
    CONSTRAINT ck_factura_estado CHECK (estado IN ('EMITIDA', 'ANULADA')),
    CONSTRAINT ck_factura_subtotal CHECK (subtotal >= 0),
    CONSTRAINT ck_factura_descuento CHECK (descuento >= 0),
    CONSTRAINT ck_factura_total CHECK (total >= 0)
);

CREATE INDEX idx_factura_cliente ON factura (cliente_id, fecha DESC);
CREATE INDEX idx_factura_estado ON factura (estado);

CREATE TABLE detalle_factura (
    id                     BIGSERIAL PRIMARY KEY,
    factura_id             BIGINT         NOT NULL REFERENCES factura (id),
    producto_id            BIGINT         NOT NULL REFERENCES producto (id),
    producto_nombre        VARCHAR(120)   NOT NULL,
    cantidad               NUMERIC(12,3)  NOT NULL,
    precio_unitario        NUMERIC(12,2)  NOT NULL,
    porcentaje_descuento   NUMERIC(5,2)   NOT NULL DEFAULT 0,
    subtotal               NUMERIC(14,2)  NOT NULL,
    descuento              NUMERIC(14,2)  NOT NULL,
    total                  NUMERIC(14,2)  NOT NULL,
    CONSTRAINT ck_detalle_factura_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_factura_precio_unitario CHECK (precio_unitario >= 0),
    CONSTRAINT ck_detalle_factura_porcentaje_descuento CHECK (porcentaje_descuento >= 0 AND porcentaje_descuento <= 100),
    CONSTRAINT ck_detalle_factura_subtotal CHECK (subtotal >= 0),
    CONSTRAINT ck_detalle_factura_descuento CHECK (descuento >= 0),
    CONSTRAINT ck_detalle_factura_total CHECK (total >= 0)
);

CREATE INDEX idx_detalle_factura_factura ON detalle_factura (factura_id);
CREATE INDEX idx_detalle_factura_producto ON detalle_factura (producto_id);

CREATE TABLE historial_factura (
    id          BIGSERIAL PRIMARY KEY,
    factura_id  BIGINT        NOT NULL REFERENCES factura (id),
    tipo_evento VARCHAR(30)   NOT NULL,
    descripcion VARCHAR(300)  NOT NULL,
    usuario_id  BIGINT        NOT NULL REFERENCES usuario (id),
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT ck_historial_factura_tipo_evento CHECK (tipo_evento IN ('CREACION', 'ANULACION'))
);

CREATE INDEX idx_historial_factura_factura ON historial_factura (factura_id, created_at DESC);
