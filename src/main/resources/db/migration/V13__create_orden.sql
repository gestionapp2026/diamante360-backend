-- ============================================================================
-- V13: Modulo de ordenes/pedidos anticipados. Un vendedor en campo registra
-- aqui la intencion de venta de un cliente (por ejemplo, mercancia pedida
-- para el dia siguiente) para que se despache mas adelante. No es una
-- factura ni afecta inventario; el descuento de stock solo ocurre cuando,
-- eventualmente, se genera la factura real (fuera del alcance de este modulo).
-- ============================================================================

CREATE SEQUENCE seq_numero_orden START WITH 1 INCREMENT BY 1;

CREATE TABLE orden (
    id              BIGSERIAL PRIMARY KEY,
    numero          VARCHAR(20)  NOT NULL,
    cliente_id      BIGINT       NOT NULL REFERENCES cliente (id),
    cliente_nombre  VARCHAR(150) NOT NULL,
    fecha_creacion  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    fecha_entrega   DATE         NOT NULL,
    estado          VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    observaciones   TEXT,
    usuario_id      BIGINT       NOT NULL REFERENCES usuario (id),
    usuario_nombre  VARCHAR(150) NOT NULL,
    fecha_despacho  TIMESTAMPTZ,
    fecha_anulacion TIMESTAMPTZ,
    version         INTEGER      NOT NULL DEFAULT 0,
    CONSTRAINT uq_orden_numero UNIQUE (numero),
    CONSTRAINT ck_orden_estado CHECK (estado IN ('PENDIENTE', 'DESPACHADA', 'ANULADA'))
);

CREATE INDEX idx_orden_cliente ON orden (cliente_id);
CREATE INDEX idx_orden_estado ON orden (estado);
CREATE INDEX idx_orden_fecha_entrega ON orden (fecha_entrega);

CREATE TABLE detalle_orden (
    id               BIGSERIAL PRIMARY KEY,
    orden_id         BIGINT        NOT NULL REFERENCES orden (id),
    producto_id      BIGINT        NOT NULL REFERENCES producto (id),
    producto_nombre  VARCHAR(120)  NOT NULL,
    cantidad         NUMERIC(12,3) NOT NULL,
    CONSTRAINT ck_detalle_orden_cantidad CHECK (cantidad > 0)
);

CREATE INDEX idx_detalle_orden_orden ON detalle_orden (orden_id);
