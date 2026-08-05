-- ============================================================================
-- V10: Precios especiales por cliente. Permite fijar, para un cliente y
-- producto puntuales, un precio de venta distinto al precio_venta estandar
-- del producto (ej. acuerdos comerciales). Si no existe registro para el
-- par (cliente, producto), se usa el precio_venta normal del producto.
-- ============================================================================

CREATE TABLE precio_cliente_producto (
    id          BIGSERIAL       PRIMARY KEY,
    cliente_id  BIGINT          NOT NULL REFERENCES cliente (id),
    producto_id BIGINT          NOT NULL REFERENCES producto (id),
    precio      NUMERIC(12,2)   NOT NULL,
    version     INTEGER         NOT NULL DEFAULT 0,
    CONSTRAINT uq_precio_cliente_producto UNIQUE (cliente_id, producto_id),
    CONSTRAINT ck_precio_cliente_producto_precio CHECK (precio >= 0)
);

CREATE INDEX idx_precio_cliente_producto_cliente ON precio_cliente_producto (cliente_id);

-- ----------------------------------------------------------------------------
-- Nota: reutiliza los permisos CLIENTE_LEER y CLIENTE_EDITAR ya sembrados en
-- V1__create_seguridad.sql (no se crean permisos nuevos).
-- ----------------------------------------------------------------------------
