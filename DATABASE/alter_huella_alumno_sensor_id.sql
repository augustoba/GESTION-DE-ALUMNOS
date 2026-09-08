-- ================================================================
--  GESTIÓN DE ALUMNOS - IES Alfredo Coviello
--  Migración: agregar sensor_id a huella_alumno
--  Fecha: 2026-08-19
--
--  El Arduino ESP32 (con sensor AS608/FPM10A) guarda los templates
--  de huella internamente y asigna un ID de slot entero (sensorId).
--  Este campo almacena ese ID para poder identificar al alumno
--  cuando el Arduino envía sensorId al marcar asistencia.
-- ================================================================

USE GESTIONALUMNOS;

-- Agregar la columna sensor_id (slot en la memoria del sensor Arduino)
-- UNIQUE: cada slot del sensor corresponde a un único alumno
-- NULL: porque los registros existentes no tienen sensor_id aún
ALTER TABLE huella_alumno
    ADD COLUMN sensor_id INT NULL UNIQUE AFTER alumno_id;

-- Relajar template_data para que no sea obligatorio (el template vive en el Arduino)
ALTER TABLE huella_alumno
    MODIFY COLUMN template_data LONGBLOB NULL;

-- Índice explícito para búsquedas rápidas por sensor_id (toma de asistencia)
ALTER TABLE huella_alumno
    ADD INDEX idx_ha_sensor_id (sensor_id);

-- Verificación
SELECT
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_KEY
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'GESTIONALUMNOS'
  AND TABLE_NAME   = 'huella_alumno'
ORDER BY ORDINAL_POSITION;
