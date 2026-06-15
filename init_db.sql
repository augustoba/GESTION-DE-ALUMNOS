-- ================================================================
--  GESTIÓN DE ALUMNOS - IES Alfredo Coviello
--  Script de creación de base de datos (v2 - rediseño completo)
--  Base: GESTIONALUMNOS  |  Motor: MySQL 8+
-- ================================================================

CREATE DATABASE IF NOT EXISTS GESTIONALUMNOS
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE GESTIONALUMNOS;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS envio_mail;
DROP TABLE IF EXISTS calendario_academico;
DROP TABLE IF EXISTS configuracion_asistencia;
DROP TABLE IF EXISTS asistencia;
DROP TABLE IF EXISTS horario_clase;
DROP TABLE IF EXISTS huella_alumno;
DROP TABLE IF EXISTS arduino;
DROP TABLE IF EXISTS aula;
DROP TABLE IF EXISTS inscripcion_materia;
DROP TABLE IF EXISTS periodo_inscripcion;
DROP TABLE IF EXISTS turno_asignado;
DROP TABLE IF EXISTS configuracion_turno;
DROP TABLE IF EXISTS documento_digital;
DROP TABLE IF EXISTS pago;
DROP TABLE IF EXISTS documento_checklist;
DROP TABLE IF EXISTS preinscripcion;
DROP TABLE IF EXISTS materia;
DROP TABLE IF EXISTS anio_carrera;
DROP TABLE IF EXISTS carrera;
DROP TABLE IF EXISTS docente;
DROP TABLE IF EXISTS alumno;
DROP TABLE IF EXISTS usuario_permiso;
DROP TABLE IF EXISTS permiso;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS rol;

SET FOREIGN_KEY_CHECKS = 1;

-- ================================================================
--  ROLES Y USUARIOS
-- ================================================================

CREATE TABLE rol (
    id     BIGINT      NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE,   -- SUPER_ADMIN | ADMIN | DOCENTE | ALUMNO
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE usuario (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol_id   BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES rol(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  Permisos granulares que el SUPER_ADMIN puede otorgar a un ADMIN
--  Los códigos son un enum en Java: HABILITAR_ALUMNO, REGISTRAR_PAGO, etc.
-- ----------------------------------------------------------------
CREATE TABLE permiso (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    codigo      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE usuario_permiso (
    id           BIGINT   NOT NULL AUTO_INCREMENT,
    usuario_id   BIGINT   NOT NULL,
    permiso_id   BIGINT   NOT NULL,
    fecha_desde  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_hasta  DATETIME,                  -- NULL = sin vencimiento
    otorgado_por BIGINT   NOT NULL,         -- FK al usuario SUPER_ADMIN
    PRIMARY KEY (id),
    CONSTRAINT fk_up_usuario    FOREIGN KEY (usuario_id)   REFERENCES usuario(id),
    CONSTRAINT fk_up_permiso    FOREIGN KEY (permiso_id)   REFERENCES permiso(id),
    CONSTRAINT fk_up_otorgado   FOREIGN KEY (otorgado_por) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================================
--  ESTRUCTURA ACADÉMICA
-- ================================================================

CREATE TABLE carrera (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(100) NOT NULL,
    descripcion    VARCHAR(500),
    activa         BOOLEAN      NOT NULL DEFAULT TRUE,
    cupo_maximo    INT          DEFAULT 0,
    prefijo_turno  VARCHAR(5),             -- A, B, C... para el sistema de turnos
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE anio_carrera (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    carrera_id  BIGINT NOT NULL,
    numero_anio INT    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ac_carrera FOREIGN KEY (carrera_id) REFERENCES carrera(id),
    UNIQUE KEY uk_anio_carrera (carrera_id, numero_anio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  aula es ahora una entidad propia (vinculada al arduino del aula)
-- ----------------------------------------------------------------
CREATE TABLE aula (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    capacidad   INT,
    descripcion VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  materia sin horario (el horario vive en horario_clase)
-- ----------------------------------------------------------------
CREATE TABLE materia (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(255) NOT NULL,
    descripcion     VARCHAR(500),
    anio_carrera_id BIGINT,
    docente_id      BIGINT,                -- docente responsable principal
    PRIMARY KEY (id),
    CONSTRAINT fk_materia_anio    FOREIGN KEY (anio_carrera_id) REFERENCES anio_carrera(id),
    CONSTRAINT fk_materia_docente FOREIGN KEY (docente_id)      REFERENCES docente(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================================
--  ALUMNO Y DOCENTE
-- ================================================================

CREATE TABLE alumno (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    nombres    VARCHAR(255) NOT NULL,
    apellidos  VARCHAR(255) NOT NULL,
    dni        VARCHAR(20)  NOT NULL UNIQUE,
    cuil       VARCHAR(20),
    email      VARCHAR(255) UNIQUE,
    telefono   VARCHAR(50),
    direccion  VARCHAR(255),
    localidad  VARCHAR(100),
    fecha_nac  DATE,
    foto_url   VARCHAR(500),
    habilitado BOOLEAN      NOT NULL DEFAULT FALSE,   -- lo activa el admin
    carrera_id BIGINT,
    usuario_id BIGINT       UNIQUE,
    PRIMARY KEY (id),
    CONSTRAINT fk_alumno_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_alumno_carrera FOREIGN KEY (carrera_id) REFERENCES carrera(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  docente.id = usuario.id  (@MapsId en JPA)
-- ----------------------------------------------------------------
CREATE TABLE docente (
    id        BIGINT       NOT NULL,
    nombres   VARCHAR(255) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    dni       VARCHAR(20)  UNIQUE,
    email     VARCHAR(255) UNIQUE,
    telefono  VARCHAR(50),
    activo    BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT fk_docente_usuario FOREIGN KEY (id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================================
--  PREINSCRIPCIÓN (formulario web → imprimible → proceso presencial)
-- ================================================================

CREATE TABLE preinscripcion (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    codigo_formulario VARCHAR(20)  NOT NULL UNIQUE,   -- PRE-2024-00042
    nombre            VARCHAR(255) NOT NULL,
    apellido          VARCHAR(255) NOT NULL,
    dni               VARCHAR(20)  NOT NULL,
    email             VARCHAR(255),
    telefono          VARCHAR(50),
    direccion         VARCHAR(255),
    localidad         VARCHAR(100),
    fecha_nacimiento  DATE,
    lugar_nacimiento  VARCHAR(100),
    nacionalidad      VARCHAR(100),
    foto_url          VARCHAR(500),                   -- obligatoria en el form
    carrera_id        BIGINT,
    estado            VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',
    -- PENDIENTE | EN_REVISION | HABILITADO | RECHAZADO
    fecha_creacion    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    alumno_id         BIGINT,                         -- se asigna al habilitar
    PRIMARY KEY (id),
    CONSTRAINT fk_pre_carrera FOREIGN KEY (carrera_id) REFERENCES carrera(id),
    CONSTRAINT fk_pre_alumno  FOREIGN KEY (alumno_id)  REFERENCES alumno(id),
    INDEX idx_pre_dni (dni),
    INDEX idx_pre_estado (estado),
    INDEX idx_pre_apellido (apellido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  Checklist de documentos presentados en el proceso presencial
--  Solo marca presencia física, sin almacenar archivos
-- ----------------------------------------------------------------
CREATE TABLE documento_checklist (
    id                 BIGINT      NOT NULL AUTO_INCREMENT,
    preinscripcion_id  BIGINT      NOT NULL,
    tipo_documento     VARCHAR(50) NOT NULL,  -- DNI_FRENTE | DNI_DORSO | TITULO | FOTO_CARNET
    presentado         BOOLEAN     NOT NULL DEFAULT FALSE,
    fecha_presentacion DATETIME,
    registrado_por     BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_dc_preinscripcion FOREIGN KEY (preinscripcion_id) REFERENCES preinscripcion(id),
    CONSTRAINT fk_dc_usuario        FOREIGN KEY (registrado_por)    REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  Registro de pago presencial (puede ser parcial)
-- ----------------------------------------------------------------
CREATE TABLE pago (
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    preinscripcion_id BIGINT         NOT NULL UNIQUE,
    estado            VARCHAR(20)    NOT NULL DEFAULT 'SIN_PAGO',  -- SIN_PAGO | PARCIAL | COMPLETO
    monto_total       DECIMAL(10,2),
    monto_abonado     DECIMAL(10,2)  NOT NULL DEFAULT 0,
    fecha_ultimo_pago DATETIME,
    registrado_por    BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_pago_pre     FOREIGN KEY (preinscripcion_id) REFERENCES preinscripcion(id),
    CONSTRAINT fk_pago_usuario FOREIGN KEY (registrado_por)    REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  Documentos digitales subidos por el alumno durante el año
-- ----------------------------------------------------------------
CREATE TABLE documento_digital (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    alumno_id        BIGINT       NOT NULL,
    tipo_documento   VARCHAR(50)  NOT NULL,
    archivo_url      VARCHAR(500),
    estado           VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',
    -- PENDIENTE | SUBIDO | VALIDADO | RECHAZADO
    fecha_subida     DATETIME,
    fecha_validacion DATETIME,
    validado_por     BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_dd_alumno  FOREIGN KEY (alumno_id)    REFERENCES alumno(id),
    CONSTRAINT fk_dd_usuario FOREIGN KEY (validado_por) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================================
--  SISTEMA DE TURNOS (inscripción presencial)
-- ================================================================

CREATE TABLE configuracion_turno (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nombre            VARCHAR(100) NOT NULL,   -- "Inscripción Presencial 2024"
    fecha             DATE         NOT NULL,
    horario_inicio    TIME         NOT NULL,
    horario_fin       TIME         NOT NULL,
    intervalo_minutos INT          NOT NULL DEFAULT 5,
    activo            BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE turno_asignado (
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    preinscripcion_id      BIGINT       NOT NULL,
    configuracion_turno_id BIGINT       NOT NULL,
    carrera_id             BIGINT       NOT NULL,
    numero_turno           VARCHAR(10)  NOT NULL,    -- A1, B3, C12...
    hora_asignada          TIME         NOT NULL,
    confirmado             BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_confirmacion     DATETIME,
    token_confirmacion     VARCHAR(100) UNIQUE,      -- token del link en el mail
    PRIMARY KEY (id),
    CONSTRAINT fk_ta_preinscripcion  FOREIGN KEY (preinscripcion_id)      REFERENCES preinscripcion(id),
    CONSTRAINT fk_ta_configuracion   FOREIGN KEY (configuracion_turno_id) REFERENCES configuracion_turno(id),
    CONSTRAINT fk_ta_carrera         FOREIGN KEY (carrera_id)             REFERENCES carrera(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================================
--  REINSCRIPCIÓN DE MATERIAS (alumnos existentes)
-- ================================================================

CREATE TABLE periodo_inscripcion (
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(100) NOT NULL,   -- "Inscripción 2024"
    fecha_inicio DATE         NOT NULL,
    fecha_fin    DATE         NOT NULL,
    activo       BOOLEAN      NOT NULL DEFAULT FALSE,
    tipo         VARCHAR(30)  NOT NULL,   -- PREINSCRIPCION | REINSCRIPCION
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE inscripcion_materia (
    id                     BIGINT      NOT NULL AUTO_INCREMENT,
    alumno_id              BIGINT      NOT NULL,
    materia_id             BIGINT      NOT NULL,
    periodo_inscripcion_id BIGINT      NOT NULL,
    estado                 VARCHAR(30) NOT NULL DEFAULT 'SOLICITADA',
    -- SOLICITADA | CONFIRMADA | RECHAZADA
    fecha_solicitud        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion       DATETIME,
    resuelto_por           BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_inscripcion (alumno_id, materia_id, periodo_inscripcion_id),
    CONSTRAINT fk_im_alumno   FOREIGN KEY (alumno_id)              REFERENCES alumno(id),
    CONSTRAINT fk_im_materia  FOREIGN KEY (materia_id)             REFERENCES materia(id),
    CONSTRAINT fk_im_periodo  FOREIGN KEY (periodo_inscripcion_id) REFERENCES periodo_inscripcion(id),
    CONSTRAINT fk_im_usuario  FOREIGN KEY (resuelto_por)           REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================================
--  ASISTENCIA CON ARDUINO
-- ================================================================

CREATE TABLE arduino (
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    identificador_hardware VARCHAR(100) NOT NULL UNIQUE,   -- MAC o código físico
    tipo                   VARCHAR(20)  NOT NULL,          -- REGISTRO | AULA
    aula_id                BIGINT,                         -- NULL si es el de administración
    descripcion            VARCHAR(255),
    activo                 BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT fk_arduino_aula FOREIGN KEY (aula_id) REFERENCES aula(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE huella_alumno (
    id              BIGINT   NOT NULL AUTO_INCREMENT,
    alumno_id       BIGINT   NOT NULL UNIQUE,
    template_data   LONGBLOB NOT NULL,            -- binario del sensor de huella
    pin_alternativo VARCHAR(10),                  -- PIN numérico de respaldo
    fecha_registro  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_ha_alumno FOREIGN KEY (alumno_id) REFERENCES alumno(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  Horarios de cada materia (una materia puede tener varios slots)
-- ----------------------------------------------------------------
CREATE TABLE horario_clase (
    id                 BIGINT      NOT NULL AUTO_INCREMENT,
    materia_id         BIGINT      NOT NULL,
    aula_id            BIGINT,
    docente_id         BIGINT,
    dia_semana         VARCHAR(15) NOT NULL,    -- LUNES | MARTES | ...
    hora_inicio        TIME        NOT NULL,
    hora_fin           TIME        NOT NULL,
    fecha_inicio_cursada DATE      NOT NULL,
    fecha_fin_cursada    DATE      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_hc_materia  FOREIGN KEY (materia_id)  REFERENCES materia(id),
    CONSTRAINT fk_hc_aula     FOREIGN KEY (aula_id)     REFERENCES aula(id),
    CONSTRAINT fk_hc_docente  FOREIGN KEY (docente_id)  REFERENCES docente(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE asistencia (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    alumno_id        BIGINT      NOT NULL,
    horario_clase_id BIGINT      NOT NULL,
    fecha            DATE        NOT NULL,
    hora_registro    TIME,
    estado           VARCHAR(15) NOT NULL,    -- PRESENTE | TARDANZA | AUSENTE
    metodo           VARCHAR(10),             -- HUELLA | PIN | MANUAL
    modificado_por   BIGINT,
    modificado_en    DATETIME,
    justificacion    VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_asistencia (alumno_id, horario_clase_id, fecha),
    CONSTRAINT fk_asis_alumno  FOREIGN KEY (alumno_id)        REFERENCES alumno(id),
    CONSTRAINT fk_asis_horario FOREIGN KEY (horario_clase_id) REFERENCES horario_clase(id),
    CONSTRAINT fk_asis_usuario FOREIGN KEY (modificado_por)   REFERENCES usuario(id),
    INDEX idx_asis_fecha (fecha),
    INDEX idx_asis_horario_fecha (horario_clase_id, fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  Umbral de asistencia: puede ser global, por carrera o por materia
-- ----------------------------------------------------------------
CREATE TABLE configuracion_asistencia (
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    porcentaje_minimo INT         NOT NULL DEFAULT 75,
    tolerancia_minutos INT        NOT NULL DEFAULT 15,   -- minutos antes de contar TARDANZA/AUSENTE
    aplica_a          VARCHAR(20) NOT NULL DEFAULT 'GLOBAL',   -- GLOBAL | CARRERA | MATERIA
    carrera_id        BIGINT,
    materia_id        BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_ca_carrera FOREIGN KEY (carrera_id) REFERENCES carrera(id),
    CONSTRAINT fk_ca_materia FOREIGN KEY (materia_id) REFERENCES materia(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================================
--  CALENDARIO ACADÉMICO
-- ================================================================

CREATE TABLE calendario_academico (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    fecha       DATE         NOT NULL,
    tipo        VARCHAR(20)  NOT NULL,    -- FERIADO | SUSPENSION | EVENTO
    descripcion VARCHAR(255),
    afecta_a    VARCHAR(20)  NOT NULL DEFAULT 'TODAS',   -- TODAS | CARRERA
    carrera_id  BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_cal_carrera FOREIGN KEY (carrera_id) REFERENCES carrera(id),
    INDEX idx_cal_fecha (fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================================
--  MAILING MASIVO
-- ================================================================

CREATE TABLE envio_mail (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    asunto            VARCHAR(255) NOT NULL,
    cuerpo            TEXT         NOT NULL,
    destinatario_tipo VARCHAR(30)  NOT NULL,
    -- TODOS | POR_CARRERA | POR_ANIO | DOCS_FALTANTES
    carrera_id        BIGINT,
    anio_carrera_id   BIGINT,
    total_enviados    INT          DEFAULT 0,
    enviado_en        DATETIME,
    enviado_por       BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_em_carrera  FOREIGN KEY (carrera_id)      REFERENCES carrera(id),
    CONSTRAINT fk_em_anio     FOREIGN KEY (anio_carrera_id) REFERENCES anio_carrera(id),
    CONSTRAINT fk_em_usuario  FOREIGN KEY (enviado_por)     REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================================
--  DATOS ESENCIALES
-- ================================================================

-- ── Roles ──────────────────────────────────────────────────────
INSERT INTO rol (nombre) VALUES
    ('SUPER_ADMIN'),
    ('ADMIN'),
    ('DOCENTE'),
    ('ALUMNO');

-- ── Permisos granulares (para el rol ADMIN) ────────────────────
INSERT INTO permiso (codigo, descripcion) VALUES
    ('HABILITAR_ALUMNO',    'Habilitar cuenta de alumno tras la inscripción presencial'),
    ('REGISTRAR_PAGO',      'Registrar o actualizar el pago de un alumno'),
    ('GESTIONAR_DOCUMENTOS','Ver y validar documentos de alumnos'),
    ('ENVIAR_MAILS',        'Enviar mails masivos a alumnos'),
    ('GESTIONAR_CARRERAS',  'Crear y editar carreras y materias'),
    ('GESTIONAR_MATERIAS',  'Crear y editar materias y horarios'),
    ('GESTIONAR_DOCENTES',  'Crear y editar docentes');

-- ── Super admin ────────────────────────────────────────────────
--   Email:      superadmin@coviello.com
--   Contraseña: Admin1234
INSERT INTO usuario (username, password, rol_id) VALUES
    ('superadmin@coviello.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     (SELECT id FROM rol WHERE nombre = 'SUPER_ADMIN'));

-- ── Admin general ──────────────────────────────────────────────
--   Email:      admin@coviello.com
--   Contraseña: Admin1234
INSERT INTO usuario (username, password, rol_id) VALUES
    ('admin@coviello.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     (SELECT id FROM rol WHERE nombre = 'ADMIN'));

-- ── Carreras base ──────────────────────────────────────────────
INSERT INTO carrera (nombre, descripcion, activa, cupo_maximo, prefijo_turno) VALUES
    ('Técnico Superior en Sistemas Informáticos',
     'Carrera orientada al desarrollo de software, bases de datos y redes.',
     TRUE, 30, 'A'),
    ('Técnico Superior en Diseño Gráfico',
     'Carrera orientada al diseño visual, branding e identidad corporativa.',
     TRUE, 30, 'B'),
    ('Técnico Superior en Administración de Empresas',
     'Carrera orientada a la gestión, contabilidad y organización empresarial.',
     TRUE, 30, 'C');

-- ── Años para las 3 carreras ───────────────────────────────────
INSERT INTO anio_carrera (carrera_id, numero_anio)
SELECT id, anio FROM carrera, (SELECT 1 AS anio UNION SELECT 2 UNION SELECT 3) años;

-- ── Configuración global de asistencia ────────────────────────
INSERT INTO configuracion_asistencia (porcentaje_minimo, tolerancia_minutos, aplica_a)
VALUES (75, 15, 'GLOBAL');

-- ================================================================
--  DATOS DE PRUEBA
-- ================================================================

-- ── Docente de prueba ──────────────────────────────────────────
--   Email:      docente@coviello.com
--   Contraseña: 12345678
INSERT INTO usuario (username, password, rol_id) VALUES
    ('docente@coviello.com',
     '$2a$10$P1tI8sRLDr2JO9YWomfRf.Kq/R7nGfn6R6WNLLhTbCLTaVMlQrEWi',
     (SELECT id FROM rol WHERE nombre = 'DOCENTE'));

INSERT INTO docente (id, nombres, apellidos, dni, email, telefono, activo)
SELECT id, 'María', 'González', '12345678', 'docente@coviello.com', '3514001234', TRUE
FROM usuario WHERE username = 'docente@coviello.com';

-- ── Aula de prueba ─────────────────────────────────────────────
INSERT INTO aula (nombre, capacidad, descripcion) VALUES
    ('Aula 1', 30, 'Planta baja - ala norte'),
    ('Aula 2', 25, 'Planta baja - ala sur'),
    ('Lab Informática', 20, 'Primer piso - laboratorio de PCs');

-- ── Materias 1° año Sistemas ───────────────────────────────────
INSERT INTO materia (nombre, descripcion, anio_carrera_id, docente_id)
SELECT
    'Introducción a la Programación',
    'Fundamentos de algoritmia y lógica de programación.',
    ac.id,
    d.id
FROM anio_carrera ac
JOIN carrera c ON c.id = ac.carrera_id
JOIN docente d ON d.email = 'docente@coviello.com'
WHERE c.nombre LIKE 'Técnico Superior en Sistemas%' AND ac.numero_anio = 1;

INSERT INTO materia (nombre, descripcion, anio_carrera_id, docente_id)
SELECT
    'Matemática Discreta',
    'Lógica proposicional, conjuntos y estructuras algebraicas.',
    ac.id,
    d.id
FROM anio_carrera ac
JOIN carrera c ON c.id = ac.carrera_id
JOIN docente d ON d.email = 'docente@coviello.com'
WHERE c.nombre LIKE 'Técnico Superior en Sistemas%' AND ac.numero_anio = 1;

-- ── Horarios de las materias ───────────────────────────────────
INSERT INTO horario_clase (materia_id, aula_id, docente_id, dia_semana, hora_inicio, hora_fin, fecha_inicio_cursada, fecha_fin_cursada)
SELECT
    m.id,
    a.id,
    d.id,
    'LUNES',
    '08:00:00',
    '10:00:00',
    '2024-03-01',
    '2024-11-30'
FROM materia m
JOIN aula a ON a.nombre = 'Aula 1'
JOIN docente d ON d.email = 'docente@coviello.com'
WHERE m.nombre = 'Introducción a la Programación';

INSERT INTO horario_clase (materia_id, aula_id, docente_id, dia_semana, hora_inicio, hora_fin, fecha_inicio_cursada, fecha_fin_cursada)
SELECT
    m.id,
    a.id,
    d.id,
    'MIERCOLES',
    '10:00:00',
    '12:00:00',
    '2024-03-01',
    '2024-11-30'
FROM materia m
JOIN aula a ON a.nombre = 'Aula 2'
JOIN docente d ON d.email = 'docente@coviello.com'
WHERE m.nombre = 'Matemática Discreta';

-- ── Preinscripción de prueba ───────────────────────────────────
INSERT INTO preinscripcion
    (codigo_formulario, nombre, apellido, dni, email, telefono,
     direccion, localidad, fecha_nacimiento, carrera_id, estado, foto_url)
SELECT
    'PRE-2024-00001', 'Juan', 'Pérez', '30111222', 'alumno@ejemplo.com',
    '3515556677', 'Av. Colón 1234', 'Córdoba', '2000-05-15',
    c.id, 'PENDIENTE', NULL
FROM carrera c WHERE c.nombre LIKE 'Técnico Superior en Sistemas%';

-- ── Configuración de turno de prueba ──────────────────────────
INSERT INTO configuracion_turno (nombre, fecha, horario_inicio, horario_fin, intervalo_minutos, activo)
VALUES ('Inscripción Presencial 2024', '2024-03-15', '08:00:00', '12:00:00', 5, FALSE);

-- ── Feriado de prueba ──────────────────────────────────────────
INSERT INTO calendario_academico (fecha, tipo, descripcion, afecta_a)
VALUES ('2024-03-24', 'FERIADO', 'Día Nacional de la Memoria', 'TODAS');

-- ================================================================
--  VERIFICACIÓN
-- ================================================================
SELECT 'rol'                    AS tabla, COUNT(*) AS filas FROM rol
UNION ALL SELECT 'usuario',               COUNT(*) FROM usuario
UNION ALL SELECT 'permiso',               COUNT(*) FROM permiso
UNION ALL SELECT 'carrera',               COUNT(*) FROM carrera
UNION ALL SELECT 'anio_carrera',          COUNT(*) FROM anio_carrera
UNION ALL SELECT 'aula',                  COUNT(*) FROM aula
UNION ALL SELECT 'materia',               COUNT(*) FROM materia
UNION ALL SELECT 'horario_clase',         COUNT(*) FROM horario_clase
UNION ALL SELECT 'docente',               COUNT(*) FROM docente
UNION ALL SELECT 'preinscripcion',        COUNT(*) FROM preinscripcion
UNION ALL SELECT 'configuracion_turno',   COUNT(*) FROM configuracion_turno
UNION ALL SELECT 'calendario_academico',  COUNT(*) FROM calendario_academico
UNION ALL SELECT 'configuracion_asistencia', COUNT(*) FROM configuracion_asistencia;
