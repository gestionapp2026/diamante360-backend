-- ============================================================================
-- V5: Completa las columnas de auditoria (created_by, updated_by) en la
-- tabla ruta, que quedaron omitidas en V4 pero son requeridas por RutaEntity
-- (@CreatedBy/@LastModifiedBy vía AuditingEntityListener), siguiendo el mismo
-- patron ya usado en categoria_producto (V2).
-- ============================================================================

ALTER TABLE ruta ADD COLUMN created_by VARCHAR(50);
ALTER TABLE ruta ADD COLUMN updated_by VARCHAR(50);
