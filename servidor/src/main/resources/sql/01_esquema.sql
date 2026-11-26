-- =====================================================================
-- Esquema de la base de datos (DEC-031)
--   mysql -u radiodiagnostico -p radiodiagnostico < servidor/src/main/resources/sql/01_esquema.sql
--
-- Se puede ejecutar varias veces: no borra datos.
-- Cada caso de uso añade aquí sus tablas, en una sección propia.
-- Las pruebas del servidor ejecutan este mismo script sobre H2 (DEC-070),
-- así que solo se usa SQL compatible con H2 en modo MySQL.
-- =====================================================================

-- ---------------------------------------------------------------------
-- CU12 Gestionar pacientes (DEC-055)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS paciente (
    dni               CHAR(9)      NOT NULL,
    tarjeta_sanitaria CHAR(16)     NOT NULL,
    nombre            VARCHAR(50)  NOT NULL,
    apellidos         VARCHAR(100) NOT NULL,
    fecha_nacimiento  DATE         NULL,
    sexo              VARCHAR(6)   NULL,
    telefono          CHAR(9)      NOT NULL,
    correo            VARCHAR(100) NULL,
    direccion         VARCHAR(200) NULL,
    estado            VARCHAR(6)   NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT pk_paciente PRIMARY KEY (dni),
    CONSTRAINT uk_paciente_tarjeta UNIQUE (tarjeta_sanitaria),
    CONSTRAINT ck_paciente_sexo CHECK (sexo IN ('HOMBRE', 'MUJER')),
    CONSTRAINT ck_paciente_estado CHECK (estado IN ('ACTIVO', 'BAJA'))
);

-- ---------------------------------------------------------------------
-- CU13 Gestionar citas médicas (DEC-047, DEC-062 y DEC-066)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tipo_prueba (
    id               INT          NOT NULL AUTO_INCREMENT,
    nombre           VARCHAR(100) NOT NULL,
    duracion_minutos INT          NOT NULL,
    CONSTRAINT pk_tipo_prueba PRIMARY KEY (id),
    CONSTRAINT uk_tipo_prueba_nombre UNIQUE (nombre),
    CONSTRAINT ck_tipo_prueba_duracion CHECK (duracion_minutos > 0)
);

CREATE TABLE IF NOT EXISTS recurso (
    id     INT          NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT pk_recurso PRIMARY KEY (id),
    CONSTRAINT uk_recurso_nombre UNIQUE (nombre)
);

-- fin = inicio + duración. Se guarda para poder consultar los solapes directamente (DEC-066).
-- dni_paciente no lleva clave ajena para no atar el esquema de CU13 al de CU12: el servidor
-- comprueba que el paciente existe y está activo antes de guardar (DEC-064).
CREATE TABLE IF NOT EXISTS cita (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    dni_paciente       CHAR(9)      NOT NULL,
    id_tipo_prueba     INT          NOT NULL,
    id_recurso         INT          NOT NULL,
    inicio             DATETIME     NOT NULL,
    fin                DATETIME     NOT NULL,
    duracion_minutos   INT          NOT NULL,
    estado             VARCHAR(10)  NOT NULL DEFAULT 'PROGRAMADA',
    motivo_cancelacion VARCHAR(255) NULL,
    CONSTRAINT pk_cita PRIMARY KEY (id),
    CONSTRAINT fk_cita_tipo_prueba FOREIGN KEY (id_tipo_prueba) REFERENCES tipo_prueba (id),
    CONSTRAINT fk_cita_recurso FOREIGN KEY (id_recurso) REFERENCES recurso (id),
    CONSTRAINT ck_cita_duracion CHECK (duracion_minutos > 0),
    CONSTRAINT ck_cita_estado CHECK (estado IN ('PROGRAMADA', 'CANCELADA', 'REALIZADA')),
    INDEX idx_cita_recurso (id_recurso, inicio, fin),
    INDEX idx_cita_paciente (dni_paciente, inicio, fin)
);
