# API Endpoints — Gestión de Alumnos (IES Coviello)

**Base URL:** `http://localhost:8080`
**Autenticación:** JWT Bearer Token (salvo los marcados como *sin auth*)
**Header JWT:** `Authorization: Bearer <token>`
**Colección Postman:** ver [`/postman`](../postman) — `GestionAlumnos.postman_collection.json` + `GestionAlumnos.postman_environment.json` (134 endpoints, ya organizados en las mismas 20 carpetas que este documento).

---

## Índice

1. [Autenticación](#1-autenticación)
2. [Preinscripciones](#2-preinscripciones)
3. [Alumnos (Admin)](#3-alumnos-admin)
4. [Arduino / ESP32](#4-arduino--esp32)
5. [Asistencias](#5-asistencias)
6. [Materias](#6-materias)
7. [Inscripciones a Materias](#7-inscripciones-a-materias)
8. [Horarios de Clase](#8-horarios-de-clase)
9. [Docentes](#9-docentes)
10. [Portal Docente](#10-portal-docente)
11. [Carreras](#11-carreras)
12. [Documentos Digitales](#12-documentos-digitales)
13. [Turnos](#13-turnos)
14. [Calendario Académico](#14-calendario-académico)
15. [Configuración del Sistema](#15-configuración-del-sistema)
16. [Perfil](#16-perfil)
17. [Períodos de Inscripción](#17-períodos-de-inscripción)
18. [Permisos](#18-permisos)
19. [Envío de Emails](#19-envío-de-emails)
20. [Gestión de Usuarios Admin](#20-gestión-de-usuarios-admin)
21. [Caminos felices](#caminos-felices)
22. [Tabla de roles y accesos](#tabla-de-roles-y-accesos)
23. [Usar la colección de Postman](#usar-la-colección-de-postman)

---
---

## 1. Autenticación

Base: `/auth` — Todos los endpoints son **públicos (sin JWT)**, salvo donde se indica.

### POST `/auth/login`
Autentica y devuelve el JWT.

**Body:**
```json
{ "username": "admin@coviello.com", "password": "Admin1234" }
```

**Respuesta:**
```json
{ "mensaje": "Login exitoso", "data": { "token": "eyJhbGciOi...", "username": "admin@coviello.com", "rol": "ADMIN" } }
```

### POST `/auth/registro`
Registra un alumno. Genera contraseña y la envía por email; el DNI y el email deben ser únicos.

**Body:**
```json
{ "nombres": "Juan", "apellidos": "Pérez", "dni": "12345678", "email": "juan.perez@gmail.com", "password": "MiContraseña123" }
```

### POST `/auth/recuperar-password`
Genera una nueva contraseña aleatoria y la envía por email.

**Body:** `{ "email": "alumno@ejemplo.com" }`

### GET `/auth/validar-token?token=abc123`
Verifica que un token de activación sea válido y no haya expirado.

### POST `/auth/activar`
Establece la contraseña definitiva usando el token recibido por email al ser habilitado.

**Body:**
```json
{ "token": "abc123...", "password": "MiPassword123" }
```

### POST `/auth/cambiar-password` — *autenticado*
Cambia la contraseña del usuario logueado.

**Body:**
```json
{ "passwordActual": "actual123", "passwordNueva": "nueva12345" }
```

### POST `/auth/refresh` — *autenticado*
Renueva el JWT a partir del token vigente enviado en `Authorization`.

---
---

## 2. Preinscripciones

Base: `/api/preinscripciones`

### POST `/api/preinscripciones` — *sin auth*
Crea una nueva preinscripción desde el formulario web. Requiere que la preinscripción esté habilitada (`GET /api/configuracion/preinscripcion`), si no devuelve `403`.

**Body:**
```json
{
  "nombre": "Juan", "apellido": "Pérez", "dni": "30111222",
  "fechaNacimiento": "2000-05-15", "lugarNacimiento": "Córdoba", "nacionalidad": "Argentina",
  "direccion": "Av. Colón 1234", "localidad": "Córdoba", "telefono": "3515556677",
  "email": "juan@ejemplo.com", "fotoUrl": "https://...", "carreraId": 1
}
```

**Efecto:** genera código `PRE-YYYY-NNNNN`, crea un registro de pago vacío (`SIN_PAGO`) y envía el PDF del formulario por email.

Todos los siguientes requieren rol **ADMIN / SUPER_ADMIN**:

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/preinscripciones?page=0&size=20` | Listar paginado |
| GET | `/api/preinscripciones/estado/{estado}?page&size` | Filtrar por estado: `PENDIENTE\|EN_REVISION\|HABILITADO\|RECHAZADO` |
| GET | `/api/preinscripciones/{id}` | Detalle completo con pago y checklist |
| GET | `/api/preinscripciones/buscar/codigo?codigo=` | Búsqueda parcial por código |
| GET | `/api/preinscripciones/buscar/dni?dni=` | Búsqueda exacta por DNI |
| GET | `/api/preinscripciones/buscar/nombre?nombre=&apellido=&page&size` | Búsqueda por nombre/apellido |
| GET | `/api/preinscripciones/{id}/pdf` | Descarga el PDF del formulario |
| PUT | `/api/preinscripciones/{id}/en-revision` | Cambia estado a `EN_REVISION` |
| PUT | `/api/preinscripciones/{id}/habilitar` | Habilita al aspirante como alumno (ver camino feliz B) |
| PUT | `/api/preinscripciones/{id}/rechazar` | Rechaza la preinscripción |
| POST | `/api/preinscripciones/{id}/pago` | Registra/acumula pago en efectivo |
| GET | `/api/preinscripciones/{id}/pago` | Ver estado del pago |
| DELETE | `/api/preinscripciones/{id}/pago` | Anula el pago |
| GET | `/api/preinscripciones/{id}/checklist` | Checklist de documentos físicos |
| POST | `/api/preinscripciones/{id}/checklist/{tipo}` | Marca documento físico como presentado |
| DELETE | `/api/preinscripciones/{id}/checklist/{tipo}` | Desmarca documento físico |
| POST | `/api/preinscripciones/{id}/turno` | Asigna turno de atención y envía email |
| GET | `/api/preinscripciones/{id}/turno` | Ver turno asignado |

**Body de `PUT /{id}/habilitar`:**
```json
{ "comisionId": 1 }
```

**Body de `POST /{id}/pago`** (acumulativo — suma al monto ya abonado; estado resultante `SIN_PAGO / PARCIAL / COMPLETO`):
```json
{ "montoTotal": 50000.00, "montoAbonado": 25000.00 }
```

`{tipo}` del checklist: `DNI_FRENTE | DNI_DORSO | TITULO | FOTO_CARNET | ACTA_NACIMIENTO | PSICOFISICO | BUENA_CONDUCTA`.

---
---

## 3. Alumnos (Admin)

Base: `/api/alumnos` — Requiere rol **ADMIN / SUPER_ADMIN** en todo el controller.

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/alumnos?page=0&size=20` | Listar paginado |
| GET | `/api/alumnos/buscar?nombre=&apellido=&page&size` | Buscar por nombre/apellido |
| GET | `/api/alumnos/habilitados` | Solo alumnos habilitados |
| GET | `/api/alumnos/por-carrera/{carreraId}` | Alumnos de una carrera |
| GET | `/api/alumnos/por-comision/{comisionId}` | Alumnos de una comisión |
| GET | `/api/alumnos/{id}` | Detalle de un alumno |
| PUT | `/api/alumnos/{id}` | Actualiza datos del alumno (entidad completa) |
| PUT | `/api/alumnos/{id}/habilitar` | Habilita al alumno |
| PUT | `/api/alumnos/{id}/deshabilitar` | Deshabilita al alumno |
| POST | `/api/alumnos/{id}/reenviar-activacion` | Genera nuevo token (72hs) y reenvía el email de activación (solo si la cuenta no fue activada aún) |

> Nota: no existe un endpoint para "crear alumno directamente" — el alta de alumnos se hace exclusivamente vía habilitación de una preinscripción (`PUT /api/preinscripciones/{id}/habilitar`).

---
---

## 4. Arduino / ESP32

Base: `/api/arduino` — **Sin JWT**. El ESP32 se identifica con el header:

```
X-Arduino-Id: <identificadorHardware>
```

El `identificadorHardware` es un string único cargado en la tabla `arduino` con `activo = true` (p.ej. `ESP32-REG-01`, `ESP32-AULA-LAB`). Hay dos tipos:

- **REGISTRO** — en administración, enrola huellas.
- **AULA** — en cada aula, toma asistencia (ver [Asistencias](#5-asistencias)).

### GET `/api/arduino/ping`
El ESP32 llama a esto al arrancar para confirmar que está registrado y activo.

**Respuesta:**
```json
{
  "mensaje": "Arduino activo",
  "data": { "id": 1, "identificadorHardware": "ESP32-REG-01", "tipo": "REGISTRO", "aulaId": null, "aulaNombre": null, "activo": true }
}
```

### GET `/api/arduino/alumno/dni/{dni}` — Solo tipo REGISTRO
Busca al alumno por DNI (tipeado por el operador en el Arduino).

**Respuesta:**
```json
{
  "mensaje": "Alumno encontrado",
  "data": { "id": 5, "nombres": "Juan", "apellidos": "Pérez", "dni": "30111222", "huellaRegistrada": false, "sensorId": null }
}
```

### POST `/api/arduino/huella` — Solo tipo REGISTRO
Después de enrolar la huella, envía el mapeo `alumnoId ↔ sensorId`.

**Body:**
```json
{ "alumnoId": 5, "sensorId": 3, "pinAlternativo": "1234" }
```
- `sensorId`: slot asignado por el sensor (AS608/FPM10A).
- `pinAlternativo`: código de respaldo, opcional.

### DELETE `/api/arduino/huella/{alumnoId}` — Solo tipo REGISTRO
Elimina el mapeo de huella. Usar antes de re-enrolar o al dar de baja.

---
---

## 5. Asistencias

Base: `/api/asistencias`

### POST `/api/asistencias/arduino` — *sin auth*
El Arduino de aula (tipo AULA) envía la detección. Dos variantes:

```json
{ "identificadorArduino": "ESP32-AULA-LAB", "sensorId": 3, "metodo": "HUELLA" }
```
```json
{ "identificadorArduino": "ESP32-AULA-LAB", "pin": "1234", "metodo": "PIN" }
```

**Respuesta:**
```json
{
  "mensaje": "Asistencia registrada",
  "data": {
    "id": 42, "alumnoId": 5, "alumnoNombre": "Juan Pérez", "dni": "30111222",
    "fecha": "2026-08-31", "horaRegistro": "08:05:00", "estado": "PRESENTE",
    "metodo": "HUELLA", "justificacion": null
  }
}
```
`estado` puede ser `PRESENTE` o `TARDANZA` según la tolerancia configurada.

### GET `/api/asistencias/horario/{horarioClaseId}?fecha=2026-08-31` — ADMIN / SUPER_ADMIN / DOCENTE
Asistencias de un horario en una fecha específica.

### GET `/api/asistencias/resumen/{alumnoId}/{materiaId}?desde=&hasta=` — ADMIN / SUPER_ADMIN / DOCENTE / ALUMNO
Resumen de asistencia: totales, porcentaje y flag `libre`.

### GET `/api/asistencias/calendario/{materiaId}?desde=&hasta=` — ADMIN / SUPER_ADMIN / DOCENTE
Mapa `fecha → lista de asistencias`, para la vista calendario del docente.

### PUT `/api/asistencias/{id}` — ADMIN / SUPER_ADMIN / DOCENTE
Corrige el estado y/o agrega justificación. El método queda como `MANUAL`.

**Body:**
```json
{ "estado": "PRESENTE", "justificacion": "Alumno presentó certificado médico" }
```
`estado`: `PRESENTE | TARDANZA | AUSENTE`.

---
---

## 6. Materias

Base: `/api/materias`

| Método | Endpoint | Rol | Descripción |
|---|---|---|---|
| GET | `/api/materias` | ADMIN/SUPER_ADMIN/DOCENTE | Listar todas |
| GET | `/api/materias/anio-carrera/{anioCarreraId}` | ADMIN/SUPER_ADMIN/DOCENTE | Materias de un año de carrera |
| GET | `/api/materias/{id}` | ADMIN/SUPER_ADMIN/DOCENTE | Detalle |
| POST | `/api/materias` | ADMIN/SUPER_ADMIN | Crear |
| PUT | `/api/materias/{id}` | ADMIN/SUPER_ADMIN | Actualizar |
| DELETE | `/api/materias/{id}` | SUPER_ADMIN | Eliminar |
| PUT | `/api/materias/{id}/docente/{docenteId}` | ADMIN/SUPER_ADMIN | Asignar docente |
| DELETE | `/api/materias/{id}/docente` | ADMIN/SUPER_ADMIN | Desasignar docente |

**Body de crear/actualizar** (`docenteId` y `horarios` son opcionales; si se envía `horarios: []` en un update, se eliminan todos los horarios existentes):
```json
{
  "nombre": "Programación I",
  "descripcion": "Introducción a la programación",
  "anioCarreraId": 1,
  "docenteId": 1,
  "horarios": [
    { "diaSemana": "LUNES", "horaInicio": "08:00:00", "horaFin": "10:00:00", "fechaInicioCursada": "2026-03-01", "fechaFinCursada": "2026-11-30" }
  ]
}
```
`diaSemana`: `LUNES | MARTES | MIERCOLES | JUEVES | VIERNES | SABADO | DOMINGO`.

---
---

## 7. Inscripciones a Materias

Base: `/api/inscripciones-materia`

### POST `/api/inscripciones-materia/alumno/{alumnoId}` — ALUMNO / ADMIN / SUPER_ADMIN
Solicita inscripción a una o más materias. Requiere un período de tipo `REINSCRIPCION` activo.

**Body:**
```json
{ "materiaIds": [1, 2, 3] }
```

### GET `/api/inscripciones-materia/alumno/{alumnoId}` — ALUMNO / ADMIN / SUPER_ADMIN / DOCENTE
Lista las inscripciones de un alumno.

### GET `/api/inscripciones-materia/materia/{materiaId}?estado=` — ADMIN / SUPER_ADMIN / DOCENTE
Lista inscripciones de una materia. `estado`: `SOLICITADA | CONFIRMADA | RECHAZADA` (default `SOLICITADA`).

### PUT `/api/inscripciones-materia/{id}/resolver` — ADMIN / SUPER_ADMIN / DOCENTE
Confirma o rechaza una solicitud.

**Body:**
```json
{ "estado": "CONFIRMADA" }
```

---
---

## 8. Horarios de Clase

Base: `/api/horarios`

| Método | Endpoint | Rol |
|---|---|---|
| GET | `/api/horarios/materia/{materiaId}` | ADMIN/SUPER_ADMIN/DOCENTE |
| GET | `/api/horarios/docente/{docenteId}` | ADMIN/SUPER_ADMIN/DOCENTE |
| POST | `/api/horarios/materia/{materiaId}` | ADMIN/SUPER_ADMIN |
| PUT | `/api/horarios/{horarioId}` | ADMIN/SUPER_ADMIN |
| DELETE | `/api/horarios/{horarioId}` | ADMIN/SUPER_ADMIN |

**Body (crear/actualizar):**
```json
{
  "diaSemana": "LUNES", "horaInicio": "08:00:00", "horaFin": "10:00:00",
  "fechaInicioCursada": "2026-03-01", "fechaFinCursada": "2026-11-30",
  "aulaId": 1, "docenteId": 1
}
```

---
---

## 9. Docentes

Base: `/api/docentes`

| Método | Endpoint | Rol | Descripción |
|---|---|---|---|
| GET | `/api/docentes` | ADMIN/SUPER_ADMIN | Listar |
| GET | `/api/docentes/{id}` | ADMIN/SUPER_ADMIN | Detalle |
| POST | `/api/docentes` | SUPER_ADMIN | Crear (contraseña inicial = DNI) |
| PUT | `/api/docentes/{id}` | SUPER_ADMIN | Actualizar |
| GET | `/api/docentes/{id}/materias` | ADMIN/SUPER_ADMIN | Materias asignadas |
| POST | `/api/docentes/{id}/materias/{materiaId}` | ADMIN/SUPER_ADMIN | Asignar materia |
| DELETE | `/api/docentes/{id}/materias/{materiaId}` | ADMIN/SUPER_ADMIN | Quitar materia |
| PATCH | `/api/docentes/{id}/estado?activo=true\|false` | SUPER_ADMIN | Alta/baja |

**Body (crear/actualizar):**
```json
{
  "nombres": "María", "apellidos": "González", "dni": "28111222",
  "email": "maria.gonzalez@coviello.edu.ar", "telefono": "3515551234",
  "materiasIds": [1, 2]
}
```

---
---

## 10. Portal Docente

Base: `/api/docente-portal` — Requiere rol **DOCENTE** en todo el controller.

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/docente-portal/mis-materias` | Materias asignadas al docente autenticado |
| GET | `/api/docente-portal/mis-alumnos` | Alumnos de las carreras del docente, con inscripción `CONFIRMADA` |
| GET | `/api/docente-portal/alumnos/{id}` | Detalle de un alumno |

---
---

## 11. Carreras

Base: `/api/carreras`

| Método | Endpoint | Rol | Descripción |
|---|---|---|---|
| GET | `/api/carreras` | *sin auth* | Carreras activas (usado en el formulario público) |
| GET | `/api/carreras/todas` | ADMIN/SUPER_ADMIN | Todas las carreras |
| GET | `/api/carreras/{id}` | ALUMNO/ADMIN/SUPER_ADMIN | Detalle simple |
| GET | `/api/carreras/{id}/detalle` | ADMIN/SUPER_ADMIN | Detalle completo con años y materias |
| POST | `/api/carreras` | SUPER_ADMIN | Crear carrera |
| PUT | `/api/carreras/{id}` | SUPER_ADMIN | Actualizar carrera |
| DELETE | `/api/carreras/{id}` | SUPER_ADMIN | Eliminar carrera |
| POST | `/api/carreras/{carreraId}/anios` | SUPER_ADMIN | Agregar año |
| DELETE | `/api/carreras/anios/{anioId}` | SUPER_ADMIN | Eliminar año (y sus materias) |
| POST | `/api/carreras/anios/{anioId}/materias` | SUPER_ADMIN | Agregar materia a un año |
| PUT | `/api/carreras/materias/{materiaId}` | SUPER_ADMIN | Actualizar materia |
| DELETE | `/api/carreras/materias/{materiaId}` | SUPER_ADMIN | Eliminar materia |
| POST | `/api/carreras/anios/{anioId}/comisiones` | SUPER_ADMIN | Agregar comisión |
| PUT | `/api/carreras/comisiones/{comisionId}` | SUPER_ADMIN | Actualizar comisión |
| DELETE | `/api/carreras/comisiones/{comisionId}` | SUPER_ADMIN | Eliminar comisión |
| GET | `/api/carreras/docentes` | ADMIN/SUPER_ADMIN | Docentes disponibles (selector frontend) |

**Body `POST/PUT` carrera:**
```json
{ "nombre": "Tecnicatura en Programación", "descripcion": "Carrera de 3 años orientada a desarrollo de software", "activa": true }
```

**Body `POST` año:** `{ "numeroAnio": 1 }`

**Body `POST/PUT` comisión:**
```json
{ "nombre": "Comisión A", "cupoMaximo": 40, "prefijoTurno": "A", "activa": true }
```

**Body `POST/PUT` materia:** igual al de [Materias](#6-materias) (`nombre`, `descripcion`, `anioCarreraId`, `docenteId`, `horarios`).

> Nota: las rutas de materia/comisión bajo `/api/carreras/**` son un ABM alternativo desde la vista de carrera; el ABM directo de materias vive en `/api/materias` (sección 6).

---
---

## 12. Documentos Digitales

Base: `/api/documentos-digitales` — documentos cargados por alumnos ya habilitados (distinto del checklist de documentos físicos de preinscripción).

| Método | Endpoint | Rol | Descripción |
|---|---|---|---|
| GET | `/api/documentos-digitales/alumno/{alumnoId}` | ADMIN/SUPER_ADMIN | Listar documentos de un alumno |
| POST | `/api/documentos-digitales/alumno/{alumnoId}?tipoDocumento=&archivoUrl=` | ALUMNO/ADMIN/SUPER_ADMIN | Registrar URL de un documento subido a almacenamiento externo |
| PUT | `/api/documentos-digitales/{id}/aprobar` | ADMIN/SUPER_ADMIN | Aprobar |
| PUT | `/api/documentos-digitales/{id}/rechazar` | ADMIN/SUPER_ADMIN | Rechazar con motivo |
| GET | `/api/documentos-digitales/{id}` | ADMIN/SUPER_ADMIN/ALUMNO | Ver documento |

`tipoDocumento`: `DNI_FRENTE | DNI_DORSO | TITULO | FOTO_CARNET | ACTA_NACIMIENTO | PSICOFISICO | BUENA_CONDUCTA`.

**Body de rechazar:**
```json
{ "motivo": "La imagen se ve borrosa, volver a subir." }
```

> Para que el propio alumno suba el **archivo** (no solo la URL), usar `POST /api/perfil/documentos/{tipo}` (sección 16), que acepta `multipart/form-data`.

---
---

## 13. Turnos

Base: `/api/turnos` — turnos de atención presencial para completar la preinscripción.

### Públicos (sin auth)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/turnos/dias-disponibles` | Días con cupo disponible |
| POST | `/api/turnos/buscar` | Buscar preinscripción por código parcial, DNI o nombre |
| POST | `/api/turnos/solicitar` | Solicitar turno; envía confirmación por email |
| GET | `/api/turnos/confirmar?token=` | Confirmar turno vía link de email |

**Body `POST /buscar`:**
```json
{ "tipoBusqueda": "DNI", "valor": "30111222", "nombre": null, "apellido": null }
```
`tipoBusqueda`: `CODIGO | DNI | NOMBRE`.

**Body `POST /solicitar`:**
```json
{ "configuracionTurnoId": 1, "preinscripcionId": null, "tipoBusqueda": "DNI", "valor": "30111222", "nombre": null, "apellido": null }
```
Si se envía `preinscripcionId`, se salta la búsqueda por texto.

### ADMIN / SUPER_ADMIN

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/turnos/configuracion` | Listar todos los días de inscripción configurados |
| POST | `/api/turnos/configuracion` | Crear día de inscripción |
| PUT | `/api/turnos/configuracion/{id}` | Actualizar día |
| DELETE | `/api/turnos/configuracion/{id}` | Eliminar día |
| POST | `/api/turnos/asignar/{preinscripcionId}` | Asignar turno manualmente |
| POST | `/api/turnos/notificar-apertura` | Notificar masivamente a aspirantes pendientes |
| GET | `/api/turnos/preinscripcion/{preinscripcionId}` | Ver turno (también accesible por ALUMNO) |

**Body `POST/PUT /configuracion`:**
```json
{
  "nombre": "Inscripción presencial - Marzo", "fecha": "2026-03-10",
  "horarioInicio": "09:00:00", "horarioFin": "13:00:00",
  "intervaloMinutos": 15, "cupoMaximo": 16
}
```

**Body `POST /notificar-apertura`:** `{ "mensaje": "Ya podés solicitar tu turno de inscripción presencial." }`

---
---

## 14. Calendario Académico

Base: `/api/calendario`

| Método | Endpoint | Rol | Descripción |
|---|---|---|---|
| GET | `/api/calendario?desde=&hasta=` | *sin auth* | Eventos por rango de fechas |
| GET | `/api/calendario/tipo/{tipo}` | *sin auth* | Eventos por tipo: `FERIADO\|SUSPENSION\|EVENTO` |
| GET | `/api/calendario/no-cursables/{carreraId}?desde=&hasta=` | ADMIN/SUPER_ADMIN/DOCENTE | Días no cursables de una carrera |
| POST | `/api/calendario` | SUPER_ADMIN | Crear evento |
| DELETE | `/api/calendario/{id}` | SUPER_ADMIN | Eliminar evento |

**Body `POST`:**
```json
{ "fecha": "2026-10-12", "tipo": "FERIADO", "descripcion": "Día del Respeto a la Diversidad Cultural", "afectaA": "TODAS", "carreraId": null }
```
`afectaA`: `TODAS | CARRERA` (con `carreraId` si aplica).

---
---

## 15. Configuración del Sistema

Base: `/api/configuracion`

| Método | Endpoint | Rol | Descripción |
|---|---|---|---|
| GET | `/api/configuracion/preinscripcion` | *sin auth* | Estado del formulario de preinscripción |
| PUT | `/api/configuracion/preinscripcion` | SUPER_ADMIN | Habilita/deshabilita la preinscripción |
| GET | `/api/configuracion/turnos` | *sin auth* | Estado de la solicitud de turnos |
| PUT | `/api/configuracion/turnos` | SUPER_ADMIN | Habilita/deshabilita turnos (envía email masivo al habilitar) |

**Body `PUT /preinscripcion`:** `{ "habilitada": true }`
**Body `PUT /turnos`:** `{ "habilitado": true }`

---
---

## 16. Perfil

Base: `/api/perfil` — datos del usuario autenticado (mayormente ALUMNO).

| Método | Endpoint | Rol | Descripción |
|---|---|---|---|
| GET | `/api/perfil` | ALUMNO/ADMIN/SUPER_ADMIN | Perfil del usuario autenticado |
| PUT | `/api/perfil` | ALUMNO | Actualiza dirección y teléfono |
| GET | `/api/perfil/documentos` | ALUMNO/ADMIN/SUPER_ADMIN | Documentos digitales del alumno autenticado |
| POST | `/api/perfil/documentos/{tipo}` | ALUMNO | Sube el **archivo** de un documento (`multipart/form-data`, campo `archivo`) |
| GET | `/api/perfil/documentos/archivo/{filename}` | *sin auth* | Sirve el archivo (usado en links de email) |
| GET | `/api/perfil/horarios` | ALUMNO | Horario semanal: materias confirmadas con día/hora/aula |
| GET | `/api/perfil/asistencias-resumen` | ALUMNO | Resumen de asistencia por materia |

**Body `PUT /api/perfil`:**
```json
{ "direccion": "Av. Colón 1234", "telefono": "3515556677" }
```

---
---

## 17. Períodos de Inscripción

Base: `/api/periodos-inscripcion`

| Método | Endpoint | Rol | Descripción |
|---|---|---|---|
| GET | `/api/periodos-inscripcion` | ADMIN/SUPER_ADMIN | Listar todos |
| GET | `/api/periodos-inscripcion/activo?tipo=` | *sin auth* | Período activo por tipo: `PREINSCRIPCION\|REINSCRIPCION` |
| POST | `/api/periodos-inscripcion` | SUPER_ADMIN | Crear (solo puede haber un período activo por tipo) |
| PUT | `/api/periodos-inscripcion/{id}/cerrar` | SUPER_ADMIN | Cerrar período activo |

**Body `POST`:**
```json
{ "nombre": "Reinscripción 2do cuatrimestre 2026", "fechaInicio": "2026-07-01", "fechaFin": "2026-07-15", "tipo": "REINSCRIPCION" }
```

> `POST /api/inscripciones-materia/alumno/{id}` (sección 7) requiere que exista un período `REINSCRIPCION` activo creado aquí.

---
---

## 18. Permisos

Base: `/api/permisos` — Requiere rol **SUPER_ADMIN** en todo el controller. Permite delegar acciones puntuales a un ADMIN.

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/permisos/usuario/{usuarioId}` | Permisos activos de un usuario |
| POST | `/api/permisos` | Otorgar permiso |
| DELETE | `/api/permisos/{id}` | Revocar (fija `fechaHasta = ahora`) |

**Body `POST`:**
```json
{ "usuarioId": 2, "codigoPermiso": "HABILITAR_ALUMNO", "fechaHasta": null }
```
`codigoPermiso`: `HABILITAR_ALUMNO | REGISTRAR_PAGO | GESTIONAR_DOCUMENTOS | ENVIAR_MAILS | GESTIONAR_CARRERAS | GESTIONAR_MATERIAS | GESTIONAR_DOCENTES`. Si `fechaHasta` es `null`, el permiso no expira.

---
---

## 19. Envío de Emails

Base: `/api/mails` — Requiere rol **ADMIN / SUPER_ADMIN** en todo el controller.

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/mails/enviar` | Envía email masivo |
| GET | `/api/mails/historial?page=0&size=20` | Historial de envíos |

**Body `POST /enviar`:**
```json
{ "asunto": "Recordatorio inscripción", "cuerpo": "Les recordamos que...", "destinatarioTipo": "POR_CARRERA", "carreraId": 1, "anioCarreraId": null }
```
`destinatarioTipo`: `TODOS | POR_CARRERA | POR_ANIO | DOCS_FALTANTES`.

---
---

## 20. Gestión de Usuarios Admin

Base: `/api/admin/usuarios` — Requiere rol **SUPER_ADMIN** en todo el controller.

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/admin/usuarios` | Listar administradores |
| POST | `/api/admin/usuarios` | Crear ADMIN o DOCENTE (contraseña aleatoria enviada por email) |
| DELETE | `/api/admin/usuarios/{id}` | Desactivar (no se puede desactivar al propio SUPER_ADMIN) |

**Body `POST`:**
```json
{ "nombres": "Ana", "apellidos": "Rodríguez", "email": "ana.rodriguez@coviello.edu.ar", "rol": "ADMIN" }
```
`rol`: `ADMIN | DOCENTE`.

> Nota: la creación de docentes también puede hacerse (con más detalle, incluyendo materias) vía `POST /api/docentes` (sección 9), que fija la contraseña inicial como el DNI en lugar de generarla al azar.

---
---

# Caminos felices

## A. Preinscripción web

```
1. El aspirante entra al formulario web.

2. GET /api/carreras
   → Carga la lista de carreras para el selector.

3. POST /api/preinscripciones
   Body: { nombre, apellido, dni, fechaNacimiento, lugarNacimiento, nacionalidad,
           direccion, localidad, telefono, email, fotoUrl, carreraId }
   → El backend:
       - Crea la preinscripción con estado PENDIENTE
       - Genera código PRE-YYYY-NNNNN
       - Crea un registro de pago vacío (SIN_PAGO)
       - Envía el PDF del formulario por email al aspirante

4. El aspirante lleva el código a la institución o espera la apertura de turnos.
```

## B. Habilitación de alumno (proceso presencial)

```
1. Admin hace login.
   POST /auth/login → obtiene JWT

2. Admin busca la preinscripción.
   GET /api/preinscripciones/buscar/dni?dni=30111222

3. Admin revisa el detalle (documentos, pago).
   GET /api/preinscripciones/{id}

4. Admin registra el pago y marca el checklist de documentos.
   POST /api/preinscripciones/{id}/pago
   POST /api/preinscripciones/{id}/checklist/{tipo}

5. Admin habilita al aspirante como alumno.
   PUT /api/preinscripciones/{id}/habilitar
   Body: { "comisionId": 1 }
   → El backend:
       - Verifica que hay cupo en la comisión
       - Crea un Usuario con rol ALUMNO y el Alumno vinculado
       - Genera un token de activación (72 hs)
       - Envía email al alumno con el link de activación
       - Cambia el estado de la preinscripción a HABILITADO

6. El alumno activa su cuenta.
   POST /auth/activar
   Body: { "token": "abc123...", "password": "MiPass123" }

7. El alumno ya puede hacer login.
   POST /auth/login
```

## C. Registro de huella dactilar (Arduino REGISTRO)

```
1. ESP32 arranca → verifica que está activo.
   GET /api/arduino/ping
   Header: X-Arduino-Id: ESP32-REG-01
   → { tipo: "REGISTRO", activo: true }

2. El operador tipea el DNI del alumno.
   GET /api/arduino/alumno/dni/30111222
   Header: X-Arduino-Id: ESP32-REG-01
   → { id: 5, nombres: "Juan", huellaRegistrada: false, sensorId: null }

3. El sensor AS608 enrola la huella y asigna el slot 3 (sensorId=3).

4. El Arduino avisa al backend del mapeo.
   POST /api/arduino/huella
   Header: X-Arduino-Id: ESP32-REG-01
   Body: { "alumnoId": 5, "sensorId": 3, "pinAlternativo": "1234" }

Nota: si el alumno ya tenía huella, borrarla primero con
DELETE /api/arduino/huella/{alumnoId} y repetir el proceso.
```

## D. Toma de asistencia (Arduino AULA)

```
1. ESP32 arranca → verifica que está activo y asociado al aula.
   GET /api/arduino/ping
   Header: X-Arduino-Id: ESP32-AULA-LAB
   → { tipo: "AULA", aulaId: 3, aulaNombre: "Lab Informática", activo: true }

2. El alumno apoya el dedo; el sensor devuelve sensorId=3.

3. El Arduino manda el presente al backend.
   POST /api/asistencias/arduino
   Body: { "identificadorArduino": "ESP32-AULA-LAB", "sensorId": 3, "metodo": "HUELLA" }
   → El backend:
       - Identifica el Arduino y su aula
       - Determina la clase vigente (HorarioClase por aula + día + hora actual)
       - Resuelve el alumno vía HuellaAlumno (sensorId → alumnoId)
       - Verifica inscripción CONFIRMADA en la materia y que no marcó antes hoy
       - Calcula estado: PRESENTE o TARDANZA según tolerancia
       - Guarda la asistencia

Variante con PIN (si falla el sensor):
   POST /api/asistencias/arduino
   Body: { "identificadorArduino": "ESP32-AULA-LAB", "pin": "1234", "metodo": "PIN" }
```

## E. Reinscripción a materias

```
1. SUPER_ADMIN abre el período.
   POST /api/periodos-inscripcion
   Body: { "nombre": "Reinscripción 2C 2026", "fechaInicio": "2026-07-01",
           "fechaFin": "2026-07-15", "tipo": "REINSCRIPCION" }

2. El alumno consulta el período activo.
   GET /api/periodos-inscripcion/activo?tipo=REINSCRIPCION

3. El alumno solicita inscripción a materias.
   POST /api/inscripciones-materia/alumno/{alumnoId}
   Body: { "materiaIds": [1, 2, 3] }
   → Cada materia queda con estado SOLICITADA

4. El docente o admin revisa solicitudes de su materia.
   GET /api/inscripciones-materia/materia/{materiaId}?estado=SOLICITADA

5. Resuelve cada una.
   PUT /api/inscripciones-materia/{id}/resolver
   Body: { "estado": "CONFIRMADA" }

6. El alumno ve su horario actualizado.
   GET /api/perfil/horarios
```

---
---

## Tabla de roles y accesos

| Rol           | Acceso                                                                       |
|---------------|-------------------------------------------------------------------------------|
| SUPER_ADMIN   | Todo, incluida la gestión de carreras/materias/comisiones, permisos, usuarios admin/docente |
| ADMIN         | Gestión operativa (preinscripciones, alumnos, documentos, mails, turnos); no puede crear carreras/materias/docentes ni gestionar permisos, salvo que se le otorguen permisos puntuales |
| DOCENTE       | Portal propio, sus materias y horarios, asistencia y resolución de inscripciones de sus materias |
| ALUMNO        | Su perfil, horario, asistencias, documentos, inscripción a materias |
| Arduino ESP32 | `/api/arduino/**` y `/api/asistencias/arduino` — sin JWT, autenticado por header `X-Arduino-Id` |

### Endpoints públicos (sin JWT) — confirmados en `SecurityConfig`

- `/auth/**`
- `/api/configuracion/preinscripcion` (GET)
- `/api/configuracion/turnos` (GET)
- `/api/carreras` (GET, listado activas)
- `/api/preinscripciones` (solo POST)
- `/api/turnos/dias-disponibles`, `/api/turnos/buscar`, `/api/turnos/solicitar`, `/api/turnos/confirmar`
- `/api/perfil/documentos/archivo/**` (GET)
- `/api/arduino/**`
- `/api/asistencias/arduino` (solo POST)
- `/swagger-ui/**`, `/v3/api-docs/**`

Cualquier otro endpoint requiere JWT válido, y además el rol correspondiente indicado en cada sección.

### Configuración de un Arduino nuevo

```sql
-- Arduino de REGISTRO (administración)
INSERT INTO arduino (identificador_hardware, tipo, aula_id, descripcion, activo)
VALUES ('ESP32-REG-01', 'REGISTRO', NULL, 'Escritorio de inscripción', TRUE);

-- Arduino de AULA (Lab Informática)
INSERT INTO arduino (identificador_hardware, tipo, aula_id, descripcion, activo)
VALUES ('ESP32-AULA-LAB', 'AULA', 3, 'Arduino del laboratorio de informática', TRUE);
```

---
---

## Usar la colección de Postman

Archivos en [`/postman`](../postman):

- `GestionAlumnos.postman_collection.json` — 134 endpoints en 20 carpetas (mismo orden que este documento).
- `GestionAlumnos.postman_environment.json` — variables `baseUrl` (`http://localhost:8080`) y `token` (vacía).
- `generate.js` — script Node que genera la colección; si se agregan endpoints nuevos al backend, actualizar este script y correr `node generate.js` en lugar de editar el `.json` a mano.

**Pasos:**

1. En Postman: **Import** → arrastrar ambos `.json` (colección + environment).
2. Seleccionar el environment "Gestión de Alumnos - Local" arriba a la derecha.
3. Ejecutar `01. Autenticación → Login`. El request tiene un test script que guarda automáticamente `data.token` en la variable de colección `token`.
4. El resto de los requests usa `Bearer {{token}}` heredado a nivel de colección — no hace falta tocar nada más.
5. Los endpoints públicos (formulario de preinscripción, turnos, carreras, Arduino, etc.) están marcados como **No Auth** explícitamente, así que funcionan aunque no haya token.
6. Los endpoints de Arduino ya traen el header `X-Arduino-Id: ESP32-REG-01` de ejemplo — cambiarlo por el identificador real cargado en la tabla `arduino`.
7. Los IDs de path (`/1`, `/5`, etc.) son de ejemplo; reemplazar por IDs reales de la base de datos local.
