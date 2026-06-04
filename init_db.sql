-- ================================================================
--  GESTIÓN DE ALUMNOS - IES Alfredo Coviello
--  Script de creación de base de datos desde cero
--  Base: GESTIONALUMNOS  |  Motor: MySQL 8+
-- ================================================================

CREATE DATABASE IF NOT EXISTS GESTIONALUMNOS
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE GESTIONALUMNOS;

-- ----------------------------------------------------------------
--  DROP en orden inverso de FK para evitar errores
-- ----------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS documento;
DROP TABLE IF EXISTS preinscripcion;
DROP TABLE IF EXISTS docente_materia;
DROP TABLE IF EXISTS materia;
DROP TABLE IF EXISTS anio_carrera;
DROP TABLE IF EXISTS carrera_docente;
DROP TABLE IF EXISTS carrera;
DROP TABLE IF EXISTS docente;
DROP TABLE IF EXISTS alumno;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS rol_permiso;
DROP TABLE IF EXISTS permiso;
DROP TABLE IF EXISTS rol;

SET FOREIGN_KEY_CHECKS = 1;

-- ================================================================
--  TABLAS
-- ================================================================

CREATE TABLE permiso (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE rol (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255),                        -- ALUMNO | DOCENTE | ADMIN
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE rol_permiso (
    rol_id     BIGINT NOT NULL,
    permiso_id BIGINT NOT NULL,
    PRIMARY KEY (rol_id, permiso_id),
    CONSTRAINT fk_rp_rol     FOREIGN KEY (rol_id)     REFERENCES rol(id),
    CONSTRAINT fk_rp_permiso FOREIGN KEY (permiso_id) REFERENCES permiso(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE usuario (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    username VARCHAR(255),
    password VARCHAR(255),
    rol_id   BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES rol(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE alumno (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    nombres    VARCHAR(255),
    apellidos  VARCHAR(255),
    dni        VARCHAR(255),
    cuil       VARCHAR(255),
    email      VARCHAR(255),
    telefono   VARCHAR(255),
    direccion  VARCHAR(255),
    fecha_nac  DATE,
    status     BIT(1),
    usuario_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_alumno_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  docente.id = usuario.id  (@MapsId)
-- ----------------------------------------------------------------
CREATE TABLE docente (
    id        BIGINT       NOT NULL,
    nombres   VARCHAR(255),
    apellidos VARCHAR(255),
    dni       VARCHAR(255),
    email     VARCHAR(255),
    telefono  VARCHAR(255),
    activo    BIT(1)       NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT fk_docente_usuario FOREIGN KEY (id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE carrera (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    activa      BIT(1)       DEFAULT 1,
    cupo_maximo INT          DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE carrera_docente (
    carrera_id BIGINT NOT NULL,
    docente_id BIGINT NOT NULL,
    PRIMARY KEY (carrera_id, docente_id),
    CONSTRAINT fk_cd_carrera FOREIGN KEY (carrera_id) REFERENCES carrera(id),
    CONSTRAINT fk_cd_docente FOREIGN KEY (docente_id) REFERENCES docente(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE anio_carrera (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    carrera_id  BIGINT NOT NULL,
    numero_anio INT    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ac_carrera FOREIGN KEY (carrera_id) REFERENCES carrera(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE materia (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(255) NOT NULL,
    descripcion    VARCHAR(500),
    anio_carrera_id BIGINT,
    docente_id     BIGINT,
    dia_semana     VARCHAR(20),
    hora_inicio    VARCHAR(10),
    hora_fin       VARCHAR(10),
    aula           VARCHAR(50),
    PRIMARY KEY (id),
    CONSTRAINT fk_materia_anio    FOREIGN KEY (anio_carrera_id) REFERENCES anio_carrera(id),
    CONSTRAINT fk_materia_docente FOREIGN KEY (docente_id)      REFERENCES docente(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  Tabla legada de la relación @ManyToMany Docente <-> Materia
-- ----------------------------------------------------------------
CREATE TABLE docente_materia (
    docente_id BIGINT NOT NULL,
    materia_id BIGINT NOT NULL,
    PRIMARY KEY (docente_id, materia_id),
    CONSTRAINT fk_dm_docente FOREIGN KEY (docente_id) REFERENCES docente(id),
    CONSTRAINT fk_dm_materia FOREIGN KEY (materia_id) REFERENCES materia(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
--  estado y tipo como VARCHAR(50) para evitar restricciones de ENUM
-- ----------------------------------------------------------------
CREATE TABLE preinscripcion (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(255),
    apellido            VARCHAR(255),
    dni                 VARCHAR(255),
    email               VARCHAR(255),
    telefono            VARCHAR(255),
    direccion           VARCHAR(255),
    fecha_nacimiento    DATE,
    carrera_id          BIGINT,
    pago_validado       BIT(1),
    documentos_completos BIT(1),
    fecha_creacion      DATETIME(6),
    estado              VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_preinscripcion_carrera FOREIGN KEY (carrera_id) REFERENCES carrera(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------------
CREATE TABLE documento (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    tipo             VARCHAR(50)  NOT NULL,
    archivo          LONGBLOB,
    nombre_archivo   VARCHAR(255),
    content_type     VARCHAR(255),
    estado           VARCHAR(50)  NOT NULL DEFAULT 'PENDIENTE',
    preinscripcion_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_documento_preinscripcion FOREIGN KEY (preinscripcion_id) REFERENCES preinscripcion(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ================================================================
--  DATOS ESENCIALES
--  (Los roles, el admin y las carreras base también los crea
--   DataInitializer al arrancar la app; este INSERT es por si
--   se ejecuta el script sin levantar la app primero.)
-- ================================================================

-- ── Roles ─────────────────────────────────────────────────────
INSERT INTO rol (nombre) VALUES
    ('ALUMNO'),
    ('DOCENTE'),
    ('ADMIN');

-- ── Usuario administrador ─────────────────────────────────────
--   Email:      admin@coviello.com
--   Contraseña: Admin1234
--   Hash BCrypt cost=10 generado con Spring BCryptPasswordEncoder
--
--   ⚠  Si el login falla borrá esta fila y reiniciá la app:
--      DELETE FROM usuario WHERE username = 'admin@coviello.com';
--      El DataInitializer la recrea con el hash correcto.
INSERT INTO usuario (username, password, rol_id) VALUES
    ('admin@coviello.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     (SELECT id FROM rol WHERE nombre = 'ADMIN'));

-- ── Carreras base ─────────────────────────────────────────────
INSERT INTO carrera (nombre, descripcion, activa, cupo_maximo) VALUES
    ('Técnico Superior en Sistemas Informáticos',
     'Carrera orientada al desarrollo de software, bases de datos y redes.',
     1, 30),
    ('Técnico Superior en Diseño Gráfico',
     'Carrera orientada al diseño visual, branding e identidad corporativa.',
     1, 30),
    ('Técnico Superior en Administración de Empresas',
     'Carrera orientada a la gestión, contabilidad y organización empresarial.',
     1, 30);


-- ================================================================
--  DATOS DE PRUEBA (opcional — comentar si no se necesitan)
--  Carga años, materias, un docente y una inscripción aprobada
--  para poder probar el portal docente.
-- ================================================================

-- ── Años para Sistemas (carrera 1) ────────────────────────────
INSERT INTO anio_carrera (carrera_id, numero_anio)
SELECT id, 1 FROM carrera WHERE nombre LIKE 'Técnico Superior en Sistemas%';
INSERT INTO anio_carrera (carrera_id, numero_anio)
SELECT id, 2 FROM carrera WHERE nombre LIKE 'Técnico Superior en Sistemas%';
INSERT INTO anio_carrera (carrera_id, numero_anio)
SELECT id, 3 FROM carrera WHERE nombre LIKE 'Técnico Superior en Sistemas%';

-- ── Docente de prueba ─────────────────────────────────────────
--   Email:      docente@coviello.com
--   Contraseña: 12345678  (DNI = contraseña inicial)
--   Hash BCrypt cost=10
INSERT INTO usuario (username, password, rol_id) VALUES
    ('docente@coviello.com',
     '$2a$10$P1tI8sRLDr2JO9YWomfRf.Kq/R7nGfn6R6WNLLhTbCLTaVMlQrEWi',
     (SELECT id FROM rol WHERE nombre = 'DOCENTE'));

INSERT INTO docente (id, nombres, apellidos, dni, email, telefono, activo)
SELECT id, 'María', 'González', '12345678', 'docente@coviello.com', '3514001234', 1
FROM usuario WHERE username = 'docente@coviello.com';

-- ── Materias 1° año Sistemas asignadas al docente ─────────────
INSERT INTO materia (nombre, descripcion, anio_carrera_id, docente_id, dia_semana, hora_inicio, hora_fin, aula)
SELECT
    'Introducción a la Programación',
    'Fundamentos de algoritmia y lógica de programación.',
    ac.id,
    d.id,
    'LUNES',
    '08:00',
    '10:00',
    'Aula 1'
FROM anio_carrera ac
JOIN carrera c ON c.id = ac.carrera_id
JOIN docente d ON d.email = 'docente@coviello.com'
WHERE c.nombre LIKE 'Técnico Superior en Sistemas%'
  AND ac.numero_anio = 1;

INSERT INTO materia (nombre, descripcion, anio_carrera_id, docente_id, dia_semana, hora_inicio, hora_fin, aula)
SELECT
    'Matemática Discreta',
    'Lógica proposicional, conjuntos y estructuras algebraicas.',
    ac.id,
    d.id,
    'MIERCOLES',
    '10:00',
    '12:00',
    'Aula 2'
FROM anio_carrera ac
JOIN carrera c ON c.id = ac.carrera_id
JOIN docente d ON d.email = 'docente@coviello.com'
WHERE c.nombre LIKE 'Técnico Superior en Sistemas%'
  AND ac.numero_anio = 1;

-- ── Preinscripción aprobada (alumno de prueba) ────────────────
INSERT INTO preinscripcion
    (nombre, apellido, dni, email, telefono, direccion, fecha_nacimiento,
     carrera_id, pago_validado, documentos_completos, fecha_creacion, estado)
SELECT
    'Juan', 'Pérez', '30111222', 'alumno@ejemplo.com',
    '3515556677', 'Av. Colón 1234', '2000-05-15',
    c.id, 1, 1, NOW(), 'APROBADA'
FROM carrera c
WHERE c.nombre LIKE 'Técnico Superior en Sistemas%';

INSERT INTO preinscripcion
    (nombre, apellido, dni, email, telefono, direccion, fecha_nacimiento,
     carrera_id, pago_validado, documentos_completos, fecha_creacion, estado)
SELECT
    'Laura', 'Martínez', '32445566', 'laura@ejemplo.com',
    '3516667788', 'Bv. San Juan 500', '2001-08-22',
    c.id, 1, 1, NOW(), 'APROBADA'
FROM carrera c
WHERE c.nombre LIKE 'Técnico Superior en Sistemas%';


-- ================================================================
--  VERIFICACIÓN
-- ================================================================
SELECT 'rol'            AS tabla, COUNT(*) AS filas FROM rol
UNION ALL
SELECT 'usuario',        COUNT(*) FROM usuario
UNION ALL
SELECT 'carrera',        COUNT(*) FROM carrera
UNION ALL
SELECT 'anio_carrera',   COUNT(*) FROM anio_carrera
UNION ALL
SELECT 'materia',        COUNT(*) FROM materia
UNION ALL
SELECT 'docente',        COUNT(*) FROM docente
UNION ALL
SELECT 'preinscripcion', COUNT(*) FROM preinscripcion;
