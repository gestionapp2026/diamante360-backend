-- ============================================================================
-- V21: el vendedor en campo puede toparse con un cliente o un producto que
-- todavia no existe en el catalogo al momento de facturar. Se le da
-- CLIENTE_CREAR y PRODUCTO_CREAR (permisos ya existentes desde V1) para que
-- pueda crearlos sobre la marcha desde el formulario de factura, sin
-- depender de un administrador. No se le da EDITAR/ELIMINAR: sigue sin
-- poder modificar ni borrar clientes/productos existentes.
-- ============================================================================

INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r CROSS JOIN permiso p
WHERE r.nombre = 'VENDEDOR' AND p.codigo IN ('CLIENTE_CREAR', 'PRODUCTO_CREAR');
