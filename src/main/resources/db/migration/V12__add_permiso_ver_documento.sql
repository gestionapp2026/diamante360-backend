-- ============================================================================
-- V12: Nuevo permiso CLIENTE_VER_DOCUMENTO - controla si el numero de
-- documento (cedula/RUC) de un cliente se expone en las respuestas de la API.
-- ============================================================================

INSERT INTO permiso (codigo, descripcion, modulo) VALUES
    ('CLIENTE_VER_DOCUMENTO', 'Ver numero de documento (cedula/RUC) de clientes', 'CLIENTE');

-- ADMIN ya recibe todos los permisos via el seed inicial (V1), pero esa
-- asignacion solo corrio para los permisos que existian en ese momento.
-- Hay que asignar explicitamente este permiso nuevo a ADMIN aqui.
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r
CROSS JOIN permiso p
WHERE r.nombre = 'ADMIN'
  AND p.codigo = 'CLIENTE_VER_DOCUMENTO';

-- VENDEDOR NO recibe este permiso a proposito: no debe ver la cedula/RUC
-- de los clientes en facturas, cartera de deudores ni ficha de cliente.
