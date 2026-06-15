# Flujos de Endpoints por Rol — IES Alfredo Coviello

Documento de referencia para el sistema de gestión de alumnos. Describe el camino feliz de cada rol, los endpoints involucrados y el estado esperado del sistema en cada paso.

> **Estado al 15/06/2026:** Los módulos implementados y en producción son: Auth (login, activación, recuperar contraseña, refresh JWT), Preinscripción, Turnos (público + admin), Alumnos, Documentos digitales, Carreras/Materias, Docentes, Portal docente, Configuración del sistema. Los módulos marcados como "pendiente" en la tabla de endpoints (Arduino, inscripciones-materia, períodos, calendario, mails masivos, permisos granulares) están diseñados pero aún no implementados.

### Decisiones simplificadas respecto al diseño original
- **N° de formulario:** es el `id` de la tabla (e.g. "5"), ya no "PRE-2026-00042". El PDF dice "N° de Formulario: 5".
- **Búsqueda por código en admin:** búsqueda parcial (`LIKE '%valor%'`), devuelve lista.
- **Búsqueda pública de turnos:** flujo en 2 pasos — buscar candidatos → elegir → asignar turno.
- **Botón de preinscripción en login:** solo visible si el formulario está habilitado (consultado al cargar).
- **Ruta de activación:** `/activar-cuenta?token=...` (no `/auth/activar`, para evitar colisión con el proxy Angular que intercepta todo `/auth/`).
- **Documentos digitales del alumno:** el alumno sube los 7 tipos desde su dashboard. Se sirven desde `/api/perfil/documentos/archivo/{uuid}` (ruta pública porque el browser no envía JWT en `<img src>`).

---

## Índice

1. [SUPER_ADMIN — Camino Feliz](#1-super_admin)
2. [ADMIN — Camino Feliz](#2-admin)
3. [DOCENTE — Camino Feliz](#3-docente)
4. [ALUMNO — Camino Feliz](#4-alumno)
5. [Flujo de Preinscripción e Inscripción (end-to-end)](#5-flujo-completo-preinscripción--inscripción)
6. [Flujo de Asistencia vía Arduino](#6-flujo-asistencia-arduino)
7. [Referencia de Endpoints](#7-referencia-de-endpoints)

---

## 1. SUPER_ADMIN

El SUPER_ADMIN es el administrador raíz del sistema. Configura la institución, crea usuarios administrativos y docentes, y gestiona los permisos granulares de los ADMIN.

### 1.1 Crear un ADMIN o DOCENTE

```
POST /api/admin/usuarios
Authorization: Bearer <token_superadmin>

{
  "nombres": "Laura",
  "apellidos": "Gómez",
  "email": "lgomez@coviello.edu.ar",
  "rol": "ADMIN"          // o "DOCENTE"
}
```

**¿Qué ocurre?**
1. Se valida que el email no exista.
2. Se genera una contraseña temporal aleatoria de 10 caracteres.
3. Se crea el `Usuario` con el rol indicado.
4. Se envía un email de bienvenida al nuevo usuario con sus credenciales temporales.

**Respuesta exitosa:** `201 Created` con el `Usuario` creado (sin contraseña).

---

### 1.2 Listar administradores/docentes

```
GET /api/admin/usuarios?rol=ADMIN
GET /api/admin/usuarios?rol=DOCENTE
Authorization: Bearer <token_superadmin>
```

---

### 1.3 Desactivar un ADMIN o DOCENTE

```
DELETE /api/admin/usuarios/{usuarioId}
Authorization: Bearer <token_superadmin>
```

**Restricción:** No puede desactivarse al propio SUPER_ADMIN.

---

### 1.4 Otorgar permiso temporal a un ADMIN

```
POST /api/permisos
Authorization: Bearer <token_superadmin>

{
  "usuarioId": 5,
  "codigoPermiso": "GESTIONAR_CARRERAS",
  "fechaHasta": "2026-12-31T23:59:59"
}
```

**¿Qué ocurre?** Se crea un `UsuarioPermiso` con vigencia hasta la fecha indicada. Si `fechaHasta` es null, el permiso no expira.

---

### 1.5 Revocar permiso

```
DELETE /api/permisos/{permisoId}
Authorization: Bearer <token_superadmin>
```

Fija `fechaHasta = ahora`, el permiso queda inactivo inmediatamente.

---

### 1.6 Configurar carreras

```
POST /api/carreras
Authorization: Bearer <token_superadmin>

{
  "nombre": "Tecnicatura en Programación",
  "descripcion": "Carrera de 3 años",
  "activa": true,
  "cupoMaximo": 40,
  "prefijoTurno": "A"
}
```

El `prefijoTurno` se usará para generar los números de turno (e.g. A1, A2, A3...).

---

### 1.7 Configurar períodos de inscripción

```
POST /api/periodos-inscripcion
Authorization: Bearer <token_superadmin>

{
  "nombre": "Preinscripción 2026",
  "fechaInicio": "2026-11-01",
  "fechaFin": "2026-12-15",
  "tipo": "PREINSCRIPCION"
}
```

**Tipos:** `PREINSCRIPCION`, `REINSCRIPCION`.
Solo puede haber un período activo por tipo al mismo tiempo.

---

### 1.8 Configurar turno de atención

```
POST /api/turnos/configuracion
Authorization: Bearer <token_superadmin>

{
  "nombre": "Turnos Diciembre 2026",
  "fecha": "2026-12-10",
  "horarioInicio": "08:00",
  "horarioFin": "13:00",
  "intervaloMinutos": 15
}
```

---

### 1.9 Configurar asistencia

```
POST /api/configuracion-asistencia
Authorization: Bearer <token_superadmin>

{
  "aplicaA": "GLOBAL",
  "toleranciaMinutos": 10,
  "porcentajeMinimo": 75
}
```

También se puede configurar a nivel `MATERIA` pasando `materiaId`.

---

### 1.10 Calendario académico

```
POST /api/calendario
Authorization: Bearer <token_superadmin>

{
  "fecha": "2026-07-09",
  "tipo": "FERIADO_NACIONAL",
  "descripcion": "Día de la Independencia",
  "afectaA": "TODOS"
}
```

---

### 1.11 Envío masivo de emails

```
POST /api/mails/enviar
Authorization: Bearer <token_superadmin>

{
  "asunto": "Inicio de clases 2026",
  "cuerpo": "Les informamos que las clases comienzan el 2 de marzo...",
  "destinatarioTipo": "TODOS"
}
```

**Tipos de destinatario:**
- `TODOS` — todos los alumnos habilitados.
- `POR_CARRERA` — requiere `carreraId`.
- `POR_ANIO` — requiere `anioCarreraId`.
- `DOCS_FALTANTES` — alumnos con documentos digitales en estado PENDIENTE.

---

## 2. ADMIN

El ADMIN gestiona el proceso diario: preinscripciones, alumnos, turnos, pagos, documentación física, inscripciones a materias y asistencias.

### 2.1 Ver preinscripciones del día

```
GET /api/preinscripciones?page=0&size=20
Authorization: Bearer <token_admin>
```

También se puede buscar (búsqueda parcial por código — devuelve lista):
```
GET /api/preinscripciones/buscar/codigo?codigo=5
GET /api/preinscripciones/buscar/dni?dni=30123456
GET /api/preinscripciones/buscar/nombre?nombre=Juan&page=0&size=10
```

> **Nota:** la búsqueda por código usa `LIKE '%valor%'`, por lo que "5" puede traer varios resultados. La lista muestra todos y el admin hace click en el correcto.

---

### 2.2 Ver detalle de una preinscripción

```
GET /api/preinscripciones/{id}
Authorization: Bearer <token_admin>
```

Devuelve: datos personales, `codigoFormulario`, `estado`, `pago` (EstadoPago, montos), `checklist` (documentos físicos).

---

### 2.3 Registrar pago en efectivo

```
POST /api/preinscripciones/{id}/pago
Authorization: Bearer <token_admin>

{
  "montoTotal": 5000.00,
  "montoAbonado": 2500.00
}
```

El estado de pago se calcula automáticamente:
- `montoAbonado == 0` → `SIN_PAGO`
- `0 < montoAbonado < montoTotal` → `PARCIAL`
- `montoAbonado >= montoTotal` → `COMPLETO`

Las sucesivas llamadas son acumulativas (se suma al `montoAbonado` existente).

---

### 2.4 Marcar documentos físicos presentados

```
POST /api/preinscripciones/{id}/checklist/DNI_FRENTE
POST /api/preinscripciones/{id}/checklist/DNI_DORSO
POST /api/preinscripciones/{id}/checklist/TITULO_SECUNDARIO
POST /api/preinscripciones/{id}/checklist/ACTA_NACIMIENTO
POST /api/preinscripciones/{id}/checklist/PSICOFISICO
POST /api/preinscripciones/{id}/checklist/BUENA_CONDUCTA
POST /api/preinscripciones/{id}/checklist/FOTO
Authorization: Bearer <token_admin>
```

Cada llamada registra el tipo de documento como presentado, junto con la fecha y el username del admin.

Para desmarcar:
```
DELETE /api/preinscripciones/{id}/checklist/{tipoDocumento}
```

---

### 2.5 Poner preinscripción en revisión

```
PUT /api/preinscripciones/{id}/en-revision
Authorization: Bearer <token_admin>
```

Estado: `PENDIENTE` → `EN_REVISION`

---

### 2.6 Habilitar alumno (aprobar preinscripción)

```
PUT /api/preinscripciones/{id}/habilitar
Authorization: Bearer <token_admin>
```

**¿Qué ocurre?**
1. Se crea el registro `Alumno` en la base de datos con `habilitado = true`.
2. El alumno queda asociado a la carrera de su preinscripción.
3. Estado de preinscripción → `HABILITADO`.
4. Se envía email al alumno informando que fue aceptado.

---

### 2.7 Rechazar preinscripción

```
PUT /api/preinscripciones/{id}/rechazar
Authorization: Bearer <token_admin>

{ "motivo": "Documentación incompleta. Falta el título secundario." }
```

Estado: → `RECHAZADO`. Se envía email con el motivo.

---

### 2.8 Asignar turno al alumno

```
POST /api/turnos/asignar/{preinscripcionId}
Authorization: Bearer <token_admin>
```

**¿Qué ocurre?**
1. Se verifica que haya una `ConfiguracionTurno` activa.
2. Se calcula el `horaAsignada = horarioInicio + (n-1) * intervaloMinutos`.
3. Se genera el `numeroTurno` = `prefijoCarrera + contador` (e.g. `A42`).
4. Se genera un `token` UUID para que el alumno confirme por email.
5. Se envía email al alumno con el turno y el link de confirmación.

---

### 2.9 Ver y resolver inscripciones a materias

```
GET /api/inscripciones-materia/pendientes
Authorization: Bearer <token_admin>

PUT /api/inscripciones-materia/{id}/resolver
Authorization: Bearer <token_admin>

{ "estado": "CONFIRMADA" }   // o "RECHAZADA"
```

---

### 2.10 Gestión de alumnos habilitados

```
GET /api/alumnos?page=0&size=20
GET /api/alumnos/buscar?nombre=Juan&page=0
GET /api/alumnos/por-carrera/{carreraId}
GET /api/alumnos/{id}
PUT /api/alumnos/{id}
PUT /api/alumnos/{id}/deshabilitar
PUT /api/alumnos/{id}/habilitar
Authorization: Bearer <token_admin>
```

---

### 2.11 Modificar asistencia manualmente

```
PUT /api/asistencias/{asistenciaId}
Authorization: Bearer <token_admin>

{
  "estado": "AUSENTE",
  "justificacion": "Presentó certificado médico"
}
```

El `metodo` queda registrado como `MANUAL`.

---

### 2.12 Revisar documentos digitales del alumno

```
GET /api/documentos-digitales/alumno/{alumnoId}
PUT /api/documentos-digitales/{id}/aprobar
PUT /api/documentos-digitales/{id}/rechazar
Authorization: Bearer <token_admin>

{ "motivo": "La imagen es ilegible" }
```

---

### 2.13 Gestión de horarios de clase

```
POST /api/materias/{materiaId}/horarios
Authorization: Bearer <token_admin>

{
  "diaSemana": "MARTES",
  "horaInicio": "08:00",
  "horaFin": "10:00",
  "fechaInicioCursada": "2026-03-10",
  "fechaFinCursada": "2026-07-15",
  "aulaId": 3,
  "docenteId": 7
}
```

```
PUT /api/materias/horarios/{horarioId}
DELETE /api/materias/horarios/{horarioId}
GET /api/materias/{materiaId}/horarios
```

---

### 2.14 Ver historial de mails enviados

```
GET /api/mails/historial?page=0&size=10
Authorization: Bearer <token_admin>
```

---

## 3. DOCENTE

El DOCENTE accede a su portal para ver sus materias, la lista de alumnos inscriptos, tomar asistencia y gestionar solicitudes de reinscripción.

### 3.1 Login

```
POST /api/auth/login

{
  "email": "jperez@coviello.edu.ar",
  "password": "ContraseñaTemporal123"
}
```

El sistema devuelve un JWT. En el primer login se recomienda cambiar la contraseña.

---

### 3.2 Ver mis materias

```
GET /api/docente-portal/mis-materias
Authorization: Bearer <token_docente>
```

Devuelve las materias donde el docente tiene al menos un `HorarioClase` asignado.

---

### 3.3 Ver alumnos de una materia

```
GET /api/docente-portal/mis-alumnos/{materiaId}
Authorization: Bearer <token_docente>
```

Devuelve los alumnos habilitados inscriptos en esa materia (estado `CONFIRMADA`).

---

### 3.4 Ver asistencia de una clase (por fecha)

```
GET /api/asistencias/horario/{horarioClaseId}?fecha=2026-04-15
Authorization: Bearer <token_docente>
```

Devuelve la lista de registros de asistencia para ese día.

---

### 3.5 Ver vista calendario de asistencia

```
GET /api/asistencias/calendario/{materiaId}?desde=2026-03-01&hasta=2026-06-30
Authorization: Bearer <token_docente>
```

Devuelve un mapa `fecha → [lista de asistencias]` para visualizar en el calendario del docente.

---

### 3.6 Ver resumen de asistencia de un alumno

```
GET /api/asistencias/resumen/{alumnoId}/{materiaId}?desde=2026-03-01&hasta=2026-06-30
Authorization: Bearer <token_docente>
```

Devuelve: total de clases, presentes, tardanzas, ausentes, porcentaje de asistencia y si el alumno está en riesgo de quedar libre.

---

### 3.7 Modificar una asistencia

```
PUT /api/asistencias/{asistenciaId}
Authorization: Bearer <token_docente>

{
  "estado": "TARDANZA",
  "justificacion": "El alumno llegó 5 minutos tarde con justificativo"
}
```

---

### 3.8 Ver solicitudes de reinscripción en sus materias

```
GET /api/inscripciones-materia/materia/{materiaId}?estado=PENDIENTE
Authorization: Bearer <token_docente>
```

---

### 3.9 Confirmar o rechazar solicitud de inscripción

```
PUT /api/inscripciones-materia/{inscripcionId}/resolver
Authorization: Bearer <token_docente>

{ "estado": "CONFIRMADA" }
```

---

### 3.10 Ver perfil propio

```
GET /api/perfil
Authorization: Bearer <token_docente>
```

---

## 4. ALUMNO

El ALUMNO sigue el flujo de preinscripción hasta quedar habilitado, luego usa el sistema para gestionar su cursada.

### 4.1 Preinscripción (sin cuenta — acceso público o con cuenta alumno)

```
POST /api/preinscripciones
// Sin autenticación o con token ALUMNO

{
  "nombre": "Juan",
  "apellido": "Pérez",
  "dni": "30123456",
  "fechaNacimiento": "2000-05-14",
  "lugarNacimiento": "Córdoba",
  "nacionalidad": "Argentina",
  "direccion": "Av. Colón 1234",
  "localidad": "Córdoba Capital",
  "telefono": "3512345678",
  "email": "juan.perez@gmail.com",
  "fotoUrl": null,
  "carreraId": 1
}
```

**¿Qué ocurre?**
1. Se guarda la entidad y se asigna `codigoFormulario = String.valueOf(id)` (ej: "5", "12"). Ya NO se usa PRE-YYYY-NNNNN.
2. Se crea un registro de `Pago` en estado `SIN_PAGO`.
3. Se envía PDF del formulario por email. El PDF muestra "N° de Formulario: 5".
4. Estado inicial: `PENDIENTE`.

---

### 4.2 Ver estado de su preinscripción

```
GET /api/preinscripciones/mi-preinscripcion
Authorization: Bearer <token_alumno>
```

Devuelve: estado actual, codigoFormulario, turno asignado (si existe), pago, checklist de documentos.

---

### 4.3 Confirmar turno por email

El alumno recibe un email con link:
```
GET /api/turnos/confirmar?token=<uuid>
```

No requiere autenticación. El sistema marca `TurnoAsignado.confirmado = true`.

---

### 4.4 Ver mi turno

```
GET /api/turnos/mi-turno
Authorization: Bearer <token_alumno>
```

---

### 4.5 Login (una vez habilitado)

```
POST /api/auth/login

{
  "email": "juan.perez@gmail.com",
  "password": "PasswordAsignado"
}
```

El ADMIN crea las credenciales cuando habilita al alumno o el alumno las recibe junto al email de habilitación.

---

### 4.6 Ver perfil

```
GET /api/perfil
Authorization: Bearer <token_alumno>
```

---

### 4.7 Solicitar inscripción a materias (reinscripción)

```
POST /api/inscripciones-materia
Authorization: Bearer <token_alumno>

{
  "materiaIds": [3, 7, 12]
}
```

**Requisitos:**
- Debe haber un período `REINSCRIPCION` activo.
- El alumno no puede tener una inscripción duplicada en la misma materia.

Estado inicial: `PENDIENTE` (hasta que el ADMIN o DOCENTE la confirme).

---

### 4.8 Ver mis inscripciones a materias

```
GET /api/inscripciones-materia/mis-inscripciones
Authorization: Bearer <token_alumno>
```

---

### 4.9 Ver mi asistencia en una materia

```
GET /api/asistencias/resumen/{miAlumnoId}/{materiaId}
Authorization: Bearer <token_alumno>
```

---

### 4.10 Subir documentos digitales

Una vez habilitado, el alumno puede subir documentos digitales para su legajo:

```
POST /api/documentos-digitales
Authorization: Bearer <token_alumno>
Content-Type: multipart/form-data

tipoDocumento: TITULO_SECUNDARIO
archivo: <file>
```

Estado inicial del documento: `PENDIENTE` (el ADMIN lo revisa y aprueba o rechaza).

---

### 4.11 Ver mis documentos digitales

```
GET /api/documentos-digitales/mis-documentos
Authorization: Bearer <token_alumno>
```

---

### 4.12 Ver calendario académico

```
GET /api/calendario?desde=2026-03-01&hasta=2026-07-31
Authorization: Bearer <token_alumno>
```

---

## 5. Flujo Completo: Preinscripción → Inscripción

```
ALUMNO                          ADMIN                         SISTEMA
  |                               |                              |
  |-- POST /preinscripciones ---->|                              |
  |                               |                    Usa ID como N° de formulario (ej: "5")
  |<-- 201 + codigoFormulario ----|                    Crea Pago SIN_PAGO
  |<-- Email con PDF -------------|                    Envía email PDF (muestra "N° 5")
  |                               |                              |
  | [Se presenta presencialmente] |                              |
  |                               |-- GET /preinscripciones -----+
  |                               |<-- Lista pendientes ---------|
  |                               |                              |
  |                               |-- GET /preinscripciones/{id}-+
  |                               |<-- Detalle completo ---------|
  |                               |                              |
  |                               |-- POST .../pago ------------>|
  |                               |   { montoTotal, montoAbonado }
  |                               |<-- Pago COMPLETO ------------|
  |                               |                              |
  |                               |-- POST .../checklist/DNI_FRENTE -> |
  |                               |-- POST .../checklist/DNI_DORSO --> |
  |                               |-- POST .../checklist/TITULO -----> |
  |                               |   [marca docs físicos]       |
  |                               |                              |
  |                               |-- PUT .../en-revision ------>|
  |                               |                   Estado: EN_REVISION
  |                               |                              |
  |                               |-- PUT .../habilitar -------->|
  |                               |                   Crea Alumno (habilitado=true)
  |                               |                   Estado: HABILITADO
  |<-- Email "¡Fuiste aceptado!" -+----------------------------->|
  |                               |                              |
  |                               |-- POST /turnos/asignar/{id}->|
  |                               |                   Genera turno A42
  |<-- Email con turno + link ----|                   15/12 08:45
  |                               |                              |
  |-- GET /turnos/confirmar?token=...|                           |
  |<-- 200 turno confirmado ------+----------------------------->|
```

---

## 6. Flujo Asistencia Arduino

```
ARDUINO (aula 3)                      BACKEND
  |                                       |
  |  [Alumno pone el dedo en el lector]   |
  |-- POST /api/asistencias/arduino ----->|
  |   {                                   |
  |     "identificadorArduino": "ARD-003",|
  |     "alumnoId": 15,                   |
  |     "metodo": "HUELLA"                |
  |   }                                   |
  |                                       |-- Busca Arduino por identificador
  |                                       |-- Verifica que tiene aula asignada
  |                                       |-- Busca HorarioClase activo en aula ahora
  |                                       |-- Verifica inscripción CONFIRMADA del alumno
  |                                       |-- Verifica que no marcó asistencia hoy
  |                                       |-- Compara horaActual vs horaInicio + tolerancia
  |                                       |   Si llega a tiempo → PRESENTE
  |                                       |   Si llegó tarde    → TARDANZA
  |<-- 200 { estado: "PRESENTE" } --------|
  |                                       |
  |  [Alumno sin huella, ingresa PIN]     |
  |-- POST /api/asistencias/arduino ----->|
  |   {                                   |
  |     "identificadorArduino": "ARD-003",|
  |     "pin": "4829",                    |
  |     "metodo": "PIN"                   |
  |   }                                   |
  |                                       |-- Busca alumno por PIN en HuellaAlumno
  |                                       |   (mismo flujo de validación)
  |<-- 200 { estado: "TARDANZA" } --------|
```

**Configuración de tolerancia:**
- Se busca primero en `ConfiguracionAsistencia` nivel `MATERIA`.
- Si no existe, se usa el valor `GLOBAL` (default: 10 minutos).

---

## 7. Referencia de Endpoints

### Auth
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| POST | `/api/auth/login` | público | Login, devuelve JWT |
| POST | `/api/auth/register` | público | Registro de alumno (crea cuenta) |

### Preinscripciones
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| POST | `/api/preinscripciones` | ALUMNO, ADMIN | Crear formulario |
| GET | `/api/preinscripciones` | ADMIN | Listar paginado |
| GET | `/api/preinscripciones/{id}` | ADMIN | Detalle con pago y checklist |
| GET | `/api/preinscripciones/{id}/pdf` | ADMIN | Descargar PDF |
| GET | `/api/preinscripciones/buscar/codigo` | ADMIN | Buscar por N° de formulario (parcial, devuelve lista) |
| GET | `/api/preinscripciones/buscar/dni` | ADMIN | Buscar por DNI |
| GET | `/api/preinscripciones/buscar/nombre` | ADMIN | Buscar por nombre paginado |
| GET | `/api/preinscripciones/mi-preinscripcion` | ALUMNO | Ver mi preinscripción |
| POST | `/api/preinscripciones/{id}/pago` | ADMIN | Registrar pago en efectivo |
| POST | `/api/preinscripciones/{id}/checklist/{tipo}` | ADMIN | Marcar doc físico presentado |
| DELETE | `/api/preinscripciones/{id}/checklist/{tipo}` | ADMIN | Desmarcar doc físico |
| PUT | `/api/preinscripciones/{id}/en-revision` | ADMIN | Cambiar a EN_REVISION |
| PUT | `/api/preinscripciones/{id}/habilitar` | ADMIN | Habilitar como alumno |
| PUT | `/api/preinscripciones/{id}/rechazar` | ADMIN | Rechazar con motivo |

### Turnos
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| GET | `/api/turnos/dias-disponibles` | público | Listar días activos con cupo |
| POST | `/api/turnos/buscar` | público | Buscar preinscripción por código parcial/DNI/nombre (devuelve lista) |
| POST | `/api/turnos/solicitar` | público | Solicitar turno (acepta `preinscripcionId` para saltar búsqueda) |
| GET | `/api/turnos/confirmar` | público | Confirmar turno por link de email |
| POST | `/api/turnos/configuracion` | ADMIN | Crear día de inscripción |
| PUT | `/api/turnos/configuracion/{id}` | ADMIN | Actualizar día |
| DELETE | `/api/turnos/configuracion/{id}` | ADMIN | Eliminar día |
| GET | `/api/turnos/configuracion` | ADMIN | Listar todos los días configurados |
| POST | `/api/turnos/asignar/{preinscripcionId}` | ADMIN | Asignar turno manualmente |
| POST | `/api/turnos/notificar-apertura` | ADMIN | Email masivo a aspirantes pendientes |

### Alumnos
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| GET | `/api/alumnos` | ADMIN | Listar paginado |
| GET | `/api/alumnos/buscar` | ADMIN | Buscar por nombre |
| GET | `/api/alumnos/habilitados` | ADMIN | Solo alumnos activos |
| GET | `/api/alumnos/por-carrera/{carreraId}` | ADMIN | Alumnos de una carrera |
| GET | `/api/alumnos/{id}` | ADMIN | Detalle de alumno |
| PUT | `/api/alumnos/{id}` | ADMIN | Actualizar datos |
| PUT | `/api/alumnos/{id}/habilitar` | ADMIN | Habilitar alumno |
| PUT | `/api/alumnos/{id}/deshabilitar` | ADMIN | Deshabilitar alumno |

### Carreras
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| GET | `/api/carreras` | todos | Listar carreras activas |
| GET | `/api/carreras/{id}` | todos | Detalle con materias |
| POST | `/api/carreras` | SUPER_ADMIN | Crear carrera |
| PUT | `/api/carreras/{id}` | SUPER_ADMIN | Actualizar |
| DELETE | `/api/carreras/{id}` | SUPER_ADMIN | Eliminar |

### Materias y Horarios
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| POST | `/api/carreras/{carreraId}/anios/{anioId}/materias` | ADMIN | Crear materia |
| PUT | `/api/materias/{id}` | ADMIN | Actualizar materia |
| GET | `/api/materias/{materiaId}/horarios` | ADMIN, DOCENTE | Ver horarios de materia |
| POST | `/api/materias/{materiaId}/horarios` | ADMIN | Agregar horario |
| PUT | `/api/materias/horarios/{horarioId}` | ADMIN | Actualizar horario |
| DELETE | `/api/materias/horarios/{horarioId}` | ADMIN | Eliminar horario |

### Inscripciones a Materias
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| POST | `/api/inscripciones-materia` | ALUMNO | Solicitar inscripción a materias |
| GET | `/api/inscripciones-materia/mis-inscripciones` | ALUMNO | Ver mis inscripciones |
| GET | `/api/inscripciones-materia/pendientes` | ADMIN | Ver solicitudes pendientes |
| GET | `/api/inscripciones-materia/materia/{materiaId}` | ADMIN, DOCENTE | Ver por materia |
| PUT | `/api/inscripciones-materia/{id}/resolver` | ADMIN, DOCENTE | Confirmar o rechazar |

### Asistencias
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| POST | `/api/asistencias/arduino` | sistema | Registrar desde Arduino |
| GET | `/api/asistencias/horario/{horarioId}` | ADMIN, DOCENTE | Lista por clase y fecha |
| GET | `/api/asistencias/resumen/{alumnoId}/{materiaId}` | ADMIN, DOCENTE, ALUMNO | Resumen de regularidad |
| GET | `/api/asistencias/calendario/{materiaId}` | ADMIN, DOCENTE | Vista calendario |
| PUT | `/api/asistencias/{id}` | ADMIN, DOCENTE | Modificar asistencia |

### Documentos Digitales
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| POST | `/api/documentos-digitales` | ALUMNO | Subir documento |
| GET | `/api/documentos-digitales/mis-documentos` | ALUMNO | Ver mis documentos |
| GET | `/api/documentos-digitales/alumno/{alumnoId}` | ADMIN | Ver documentos de alumno |
| GET | `/api/documentos-digitales/{id}/descargar` | ADMIN | Descargar archivo |
| PUT | `/api/documentos-digitales/{id}/aprobar` | ADMIN | Aprobar documento |
| PUT | `/api/documentos-digitales/{id}/rechazar` | ADMIN | Rechazar con motivo |

### Períodos de Inscripción
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| POST | `/api/periodos-inscripcion` | SUPER_ADMIN | Crear período |
| GET | `/api/periodos-inscripcion` | ADMIN | Listar |
| GET | `/api/periodos-inscripcion/activo` | todos | Ver período activo por tipo |
| PUT | `/api/periodos-inscripcion/{id}/cerrar` | SUPER_ADMIN | Cerrar período |

### Permisos (SUPER_ADMIN)
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| POST | `/api/permisos` | SUPER_ADMIN | Otorgar permiso a ADMIN |
| GET | `/api/permisos/usuario/{usuarioId}` | SUPER_ADMIN | Ver permisos activos |
| DELETE | `/api/permisos/{id}` | SUPER_ADMIN | Revocar permiso |

### Usuarios (SUPER_ADMIN)
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| POST | `/api/admin/usuarios` | SUPER_ADMIN | Crear ADMIN o DOCENTE |
| GET | `/api/admin/usuarios` | SUPER_ADMIN | Listar admins/docentes |
| DELETE | `/api/admin/usuarios/{id}` | SUPER_ADMIN | Desactivar usuario |

### Calendario Académico
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| GET | `/api/calendario` | todos | Listar por rango de fechas |
| GET | `/api/calendario/tipo/{tipo}` | todos | Filtrar por tipo |
| GET | `/api/calendario/no-cursables/{carreraId}` | DOCENTE, ADMIN | Días no cursables |
| POST | `/api/calendario` | SUPER_ADMIN | Agregar evento |
| DELETE | `/api/calendario/{id}` | SUPER_ADMIN | Eliminar evento |

### Emails Masivos
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| POST | `/api/mails/enviar` | SUPER_ADMIN, ADMIN | Enviar mail masivo |
| GET | `/api/mails/historial` | SUPER_ADMIN, ADMIN | Ver historial paginado |

### Docente Portal
| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| GET | `/api/docente-portal/mis-materias` | DOCENTE | Ver mis materias asignadas |
| GET | `/api/docente-portal/mis-alumnos/{materiaId}` | DOCENTE | Ver alumnos de mi materia |
| GET | `/api/docente-portal/alumno/{alumnoId}` | DOCENTE | Ver detalle de alumno |

---

## Estados del Sistema

### EstadoPreinscripcion
```
PENDIENTE → EN_REVISION → HABILITADO
                       ↘ RECHAZADO
```

### EstadoPago
```
SIN_PAGO → PARCIAL → COMPLETO
```

### EstadoInscripcionMateria
```
PENDIENTE → CONFIRMADA
          ↘ RECHAZADA
```

### EstadoDocumento (Digital)
```
PENDIENTE → SUBIDO → VALIDADO
                   ↘ RECHAZADO
```

### EstadoAsistencia
```
[Arduino/Manual] → PRESENTE
                → TARDANZA
                → AUSENTE
```
