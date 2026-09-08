// Generador de la colección Postman a partir de la definición de endpoints.
// Ejecutar: node generate.js  -> produce GestionAlumnos.postman_collection.json
const fs = require("fs");
const path = require("path");

let itemId = 0;
function uid() {
  itemId += 1;
  return "id-" + itemId.toString().padStart(4, "0");
}

function url(rawPath, query) {
  const clean = rawPath.startsWith("/") ? rawPath.slice(1) : rawPath;
  const segments = clean.split("/").filter(Boolean);
  const u = {
    raw: "{{baseUrl}}/" + segments.join("/") + (query && query.length ? "?" + query.map(q => `${q.key}=${q.value}`).join("&") : ""),
    host: ["{{baseUrl}}"],
    path: segments,
  };
  if (query && query.length) {
    u.query = query.map(q => ({ key: q.key, value: String(q.value), disabled: !!q.disabled, description: q.description }));
  }
  return u;
}

function jsonBody(obj) {
  return {
    mode: "raw",
    raw: JSON.stringify(obj, null, 2),
    options: { raw: { language: "json" } },
  };
}

function req({ name, method, path: p, query, body, headers, auth, description, events }) {
  const request = {
    method,
    header: headers || [],
    url: url(p, query),
    description: description || "",
  };
  if (body) request.body = jsonBody(body);
  if (auth) request.auth = auth; // { type: "noauth" } para públicos
  const item = {
    name,
    id: uid(),
    request,
    response: [],
  };
  if (events) item.event = events;
  return item;
}

const setTokenScript = [
  "if (pm.response.code === 200) {",
  "    const body = pm.response.json();",
  "    const token = body && body.data && body.data.token;",
  "    if (token) {",
  "        pm.collectionVariables.set('token', token);",
  "        console.log('Token guardado en la variable de colección \"token\".');",
  "    }",
  "}",
];

const noauth = { type: "noauth" };
const arduinoHeaders = (id) => [{ key: "X-Arduino-Id", value: id || "ESP32-REG-01", type: "text" }];

function folder(name, description, items) {
  return { name, description, item: items };
}

// ── 1. Autenticación ──────────────────────────────────────────────
const authFolder = folder("01. Autenticación", "Endpoints públicos (sin JWT).", [
  req({
    name: "Login",
    method: "POST",
    path: "/auth/login",
    auth: noauth,
    body: { username: "admin@coviello.com", password: "Admin1234" },
    description: "Autentica y devuelve el JWT. El test script guarda automáticamente `data.token` en la variable de colección `token`.",
    events: [
      {
        listen: "test",
        script: { type: "text/javascript", exec: setTokenScript },
      },
    ],
  }),
  req({
    name: "Registro de alumno",
    method: "POST",
    path: "/auth/registro",
    auth: noauth,
    body: { nombres: "Juan", apellidos: "Pérez", dni: "12345678", email: "juan.perez@gmail.com", password: "MiContraseña123" },
    description: "Registra un alumno directamente (fuera del flujo de preinscripción). Genera contraseña o usa la enviada, según implementación.",
  }),
  req({
    name: "Recuperar contraseña",
    method: "POST",
    path: "/auth/recuperar-password",
    auth: noauth,
    body: { email: "alumno@ejemplo.com" },
    description: "Genera una nueva contraseña aleatoria y la envía por email.",
  }),
  req({
    name: "Validar token de activación",
    method: "GET",
    path: "/auth/validar-token",
    auth: noauth,
    query: [{ key: "token", value: "abc123" }],
  }),
  req({
    name: "Activar cuenta",
    method: "POST",
    path: "/auth/activar",
    auth: noauth,
    body: { token: "abc123...", password: "MiPassword123" },
    description: "Establece la contraseña definitiva usando el token recibido por email al ser habilitado.",
  }),
  req({
    name: "Cambiar contraseña (autenticado)",
    method: "POST",
    path: "/auth/cambiar-password",
    body: { passwordActual: "actual123", passwordNueva: "nueva12345" },
  }),
  req({
    name: "Refrescar token",
    method: "POST",
    path: "/auth/refresh",
    headers: [{ key: "Authorization", value: "Bearer {{token}}", type: "text" }],
    description: "Devuelve un nuevo JWT a partir del token activo enviado en el header Authorization.",
    events: [
      {
        listen: "test",
        script: { type: "text/javascript", exec: setTokenScript },
      },
    ],
  }),
]);

// ── 2. Preinscripciones ───────────────────────────────────────────
const preinscripcionesFolder = folder("02. Preinscripciones", "Proceso de preinscripción web y gestión presencial (ADMIN).", [
  req({
    name: "Crear preinscripción (público)",
    method: "POST",
    path: "/api/preinscripciones",
    auth: noauth,
    body: {
      nombre: "Juan", apellido: "Pérez", dni: "30111222", fechaNacimiento: "2000-05-15",
      lugarNacimiento: "Córdoba", nacionalidad: "Argentina", direccion: "Av. Colón 1234",
      localidad: "Córdoba", telefono: "3515556677", email: "juan@ejemplo.com",
      fotoUrl: "https://...", carreraId: 1,
    },
    description: "Requiere que la preinscripción esté habilitada (GET /api/configuracion/preinscripcion). Genera código PRE-YYYY-NNNNN y envía PDF por email.",
  }),
  req({ name: "Listar preinscripciones (paginado)", method: "GET", path: "/api/preinscripciones", query: [{ key: "page", value: 0 }, { key: "size", value: 20 }] }),
  req({ name: "Listar por estado", method: "GET", path: "/api/preinscripciones/estado/PENDIENTE", query: [{ key: "page", value: 0 }, { key: "size", value: 20 }], description: "Valores de {estado}: PENDIENTE | EN_REVISION | HABILITADO | RECHAZADO." }),
  req({ name: "Detalle de preinscripción", method: "GET", path: "/api/preinscripciones/1" }),
  req({ name: "Buscar por código (parcial)", method: "GET", path: "/api/preinscripciones/buscar/codigo", query: [{ key: "codigo", value: "PRE-2026-00001" }] }),
  req({ name: "Buscar por DNI", method: "GET", path: "/api/preinscripciones/buscar/dni", query: [{ key: "dni", value: "30111222" }] }),
  req({ name: "Buscar por nombre/apellido", method: "GET", path: "/api/preinscripciones/buscar/nombre", query: [{ key: "nombre", value: "" }, { key: "apellido", value: "" }, { key: "page", value: 0 }, { key: "size", value: 20 }] }),
  req({ name: "Descargar PDF del formulario", method: "GET", path: "/api/preinscripciones/1/pdf" }),
  req({ name: "Poner en revisión", method: "PUT", path: "/api/preinscripciones/1/en-revision" }),
  req({
    name: "Habilitar como alumno",
    method: "PUT",
    path: "/api/preinscripciones/1/habilitar",
    body: { comisionId: 1 },
    description: "Crea el usuario ALUMNO, lo asigna a la comisión y envía email de activación (token 72hs).",
  }),
  req({ name: "Rechazar preinscripción", method: "PUT", path: "/api/preinscripciones/1/rechazar" }),
  req({
    name: "Registrar/actualizar pago en efectivo",
    method: "POST",
    path: "/api/preinscripciones/1/pago",
    body: { montoTotal: 50000.00, montoAbonado: 25000.00 },
    description: "Acumulativo: suma al monto ya abonado. Estado resultante: SIN_PAGO / PARCIAL / COMPLETO.",
  }),
  req({ name: "Ver estado del pago", method: "GET", path: "/api/preinscripciones/1/pago" }),
  req({ name: "Anular pago", method: "DELETE", path: "/api/preinscripciones/1/pago" }),
  req({ name: "Ver checklist de documentos físicos", method: "GET", path: "/api/preinscripciones/1/checklist" }),
  req({ name: "Marcar documento físico como presentado", method: "POST", path: "/api/preinscripciones/1/checklist/DNI_FRENTE", description: "Tipos válidos: DNI_FRENTE | DNI_DORSO | TITULO | FOTO_CARNET | ACTA_NACIMIENTO | PSICOFISICO | BUENA_CONDUCTA." }),
  req({ name: "Desmarcar documento físico", method: "DELETE", path: "/api/preinscripciones/1/checklist/DNI_FRENTE" }),
  req({ name: "Asignar turno de atención", method: "POST", path: "/api/preinscripciones/1/turno", description: "Genera el número de turno y envía email con link de confirmación." }),
  req({ name: "Ver turno asignado", method: "GET", path: "/api/preinscripciones/1/turno" }),
]);

// ── 3. Alumnos (Admin) ────────────────────────────────────────────
const alumnosFolder = folder("03. Alumnos (Admin)", "Gestión de alumnos ya habilitados. Requiere rol ADMIN o SUPER_ADMIN.", [
  req({ name: "Listar alumnos (paginado)", method: "GET", path: "/api/alumnos", query: [{ key: "page", value: 0 }, { key: "size", value: 20 }] }),
  req({ name: "Buscar por nombre/apellido", method: "GET", path: "/api/alumnos/buscar", query: [{ key: "nombre", value: "" }, { key: "apellido", value: "" }, { key: "page", value: 0 }, { key: "size", value: 20 }] }),
  req({ name: "Listar alumnos habilitados", method: "GET", path: "/api/alumnos/habilitados" }),
  req({ name: "Listar alumnos por carrera", method: "GET", path: "/api/alumnos/por-carrera/1" }),
  req({ name: "Listar alumnos por comisión", method: "GET", path: "/api/alumnos/por-comision/1" }),
  req({ name: "Obtener alumno por ID", method: "GET", path: "/api/alumnos/1" }),
  req({
    name: "Actualizar alumno",
    method: "PUT",
    path: "/api/alumnos/1",
    body: { nombres: "Juan", apellidos: "Pérez", dni: "30111222", email: "juan@ejemplo.com", telefono: "3515556677", direccion: "Av. Colón 1234" },
    description: "Recibe la entidad Alumno completa (model).",
  }),
  req({ name: "Habilitar alumno", method: "PUT", path: "/api/alumnos/1/habilitar" }),
  req({ name: "Deshabilitar alumno", method: "PUT", path: "/api/alumnos/1/deshabilitar" }),
  req({ name: "Reenviar email de activación", method: "POST", path: "/api/alumnos/1/reenviar-activacion", description: "Genera un nuevo token (72hs) y reenvía el email de activación. Solo si la cuenta aún no fue activada." }),
]);

// ── 4. Arduino / ESP32 ────────────────────────────────────────────
const arduinoFolder = folder("04. Arduino / ESP32", "Sin JWT. Autenticación por header X-Arduino-Id. Tipos: REGISTRO (enrola huellas) y AULA (toma asistencia).", [
  req({ name: "Ping (verificar activo)", method: "GET", path: "/api/arduino/ping", auth: noauth, headers: arduinoHeaders("ESP32-REG-01") }),
  req({ name: "Buscar alumno por DNI (solo REGISTRO)", method: "GET", path: "/api/arduino/alumno/dni/30111222", auth: noauth, headers: arduinoHeaders("ESP32-REG-01") }),
  req({
    name: "Registrar huella (solo REGISTRO)",
    method: "POST",
    path: "/api/arduino/huella",
    auth: noauth,
    headers: arduinoHeaders("ESP32-REG-01"),
    body: { alumnoId: 5, sensorId: 3, pinAlternativo: "1234" },
  }),
  req({ name: "Eliminar huella de un alumno (solo REGISTRO)", method: "DELETE", path: "/api/arduino/huella/5", auth: noauth, headers: arduinoHeaders("ESP32-REG-01") }),
]);

// ── 5. Asistencias ─────────────────────────────────────────────────
const asistenciasFolder = folder("05. Asistencias", "Registro vía Arduino/manual y consultas de regularidad.", [
  req({
    name: "Registrar asistencia desde Arduino (método HUELLA)",
    method: "POST",
    path: "/api/asistencias/arduino",
    auth: noauth,
    body: { identificadorArduino: "ESP32-AULA-LAB", sensorId: 3, metodo: "HUELLA" },
    description: "Endpoint del Arduino de AULA. Sin JWT.",
  }),
  req({
    name: "Registrar asistencia desde Arduino (método PIN)",
    method: "POST",
    path: "/api/asistencias/arduino",
    auth: noauth,
    body: { identificadorArduino: "ESP32-AULA-LAB", pin: "1234", metodo: "PIN" },
  }),
  req({ name: "Listar asistencias por horario y fecha", method: "GET", path: "/api/asistencias/horario/1", query: [{ key: "fecha", value: "2026-08-31" }], description: "ADMIN / SUPER_ADMIN / DOCENTE." }),
  req({ name: "Resumen de asistencia alumno-materia", method: "GET", path: "/api/asistencias/resumen/1/1", query: [{ key: "desde", value: "2026-03-01" }, { key: "hasta", value: "2026-11-30" }], description: "ADMIN / SUPER_ADMIN / DOCENTE / ALUMNO." }),
  req({ name: "Vista calendario de asistencias de una materia", method: "GET", path: "/api/asistencias/calendario/1", query: [{ key: "desde", value: "2026-03-01" }, { key: "hasta", value: "2026-11-30" }], description: "ADMIN / SUPER_ADMIN / DOCENTE." }),
  req({
    name: "Modificar asistencia manualmente",
    method: "PUT",
    path: "/api/asistencias/1",
    body: { estado: "PRESENTE", justificacion: "Alumno presentó certificado médico" },
    description: "ADMIN / SUPER_ADMIN / DOCENTE. estado: PRESENTE | TARDANZA | AUSENTE. El método queda como MANUAL.",
  }),
]);

// ── 6. Materias ────────────────────────────────────────────────────
const materiasFolder = folder("06. Materias", "ABM de materias y asignación de horarios/docentes.", [
  req({ name: "Listar todas las materias", method: "GET", path: "/api/materias" }),
  req({ name: "Listar materias por año de carrera", method: "GET", path: "/api/materias/anio-carrera/1" }),
  req({ name: "Obtener materia por ID", method: "GET", path: "/api/materias/1" }),
  req({
    name: "Crear materia",
    method: "POST",
    path: "/api/materias",
    body: {
      nombre: "Programación I", descripcion: "Introducción a la programación", anioCarreraId: 1, docenteId: 1,
      horarios: [{ diaSemana: "LUNES", horaInicio: "08:00:00", horaFin: "10:00:00", fechaInicioCursada: "2026-03-01", fechaFinCursada: "2026-11-30" }],
    },
    description: "docenteId y horarios son opcionales.",
  }),
  req({
    name: "Actualizar materia",
    method: "PUT",
    path: "/api/materias/1",
    body: {
      nombre: "Programación I", descripcion: "Introducción a la programación", anioCarreraId: 1, docenteId: 1,
      horarios: [{ diaSemana: "LUNES", horaInicio: "08:00:00", horaFin: "10:00:00", fechaInicioCursada: "2026-03-01", fechaFinCursada: "2026-11-30" }],
    },
    description: "Si se envía la lista de horarios vacía, se eliminan todos los horarios existentes.",
  }),
  req({ name: "Eliminar materia", method: "DELETE", path: "/api/materias/1", description: "Solo SUPER_ADMIN." }),
  req({ name: "Asignar docente a materia", method: "PUT", path: "/api/materias/1/docente/1" }),
  req({ name: "Desasignar docente de materia", method: "DELETE", path: "/api/materias/1/docente" }),
]);

// ── 7. Inscripciones a Materias ───────────────────────────────────
const inscripcionesMateriaFolder = folder("07. Inscripciones a Materias", "Solicitudes de (re)inscripción del alumno y resolución por ADMIN/DOCENTE.", [
  req({
    name: "Solicitar inscripción a materias",
    method: "POST",
    path: "/api/inscripciones-materia/alumno/1",
    body: { materiaIds: [1, 2, 3] },
    description: "ALUMNO / ADMIN / SUPER_ADMIN. Requiere un período de tipo REINSCRIPCION activo.",
  }),
  req({ name: "Ver inscripciones de un alumno", method: "GET", path: "/api/inscripciones-materia/alumno/1" }),
  req({ name: "Ver inscripciones de una materia", method: "GET", path: "/api/inscripciones-materia/materia/1", query: [{ key: "estado", value: "SOLICITADA", description: "SOLICITADA | CONFIRMADA | RECHAZADA (opcional, default SOLICITADA)" }] }),
  req({
    name: "Confirmar o rechazar solicitud",
    method: "PUT",
    path: "/api/inscripciones-materia/1/resolver",
    body: { estado: "CONFIRMADA" },
    description: "ADMIN / SUPER_ADMIN / DOCENTE. estado: CONFIRMADA | RECHAZADA.",
  }),
]);

// ── 8. Horarios de Clase ──────────────────────────────────────────
const horariosFolder = folder("08. Horarios de Clase", "CRUD de horarios de cursada por materia.", [
  req({ name: "Listar horarios de una materia", method: "GET", path: "/api/horarios/materia/1" }),
  req({ name: "Listar horarios de un docente", method: "GET", path: "/api/horarios/docente/1" }),
  req({
    name: "Crear horario para una materia",
    method: "POST",
    path: "/api/horarios/materia/1",
    body: { diaSemana: "LUNES", horaInicio: "08:00:00", horaFin: "10:00:00", fechaInicioCursada: "2026-03-01", fechaFinCursada: "2026-11-30", aulaId: 1, docenteId: 1 },
  }),
  req({
    name: "Actualizar horario",
    method: "PUT",
    path: "/api/horarios/1",
    body: { diaSemana: "MARTES", horaInicio: "10:00:00", horaFin: "12:00:00", fechaInicioCursada: "2026-03-01", fechaFinCursada: "2026-11-30", aulaId: 1, docenteId: 1 },
  }),
  req({ name: "Eliminar horario", method: "DELETE", path: "/api/horarios/1" }),
]);

// ── 9. Docentes ────────────────────────────────────────────────────
const docentesFolder = folder("09. Docentes", "Gestión de docentes.", [
  req({ name: "Listar docentes", method: "GET", path: "/api/docentes" }),
  req({ name: "Obtener docente por ID", method: "GET", path: "/api/docentes/1" }),
  req({
    name: "Crear docente",
    method: "POST",
    path: "/api/docentes",
    body: { nombres: "María", apellidos: "González", dni: "28111222", email: "maria.gonzalez@coviello.edu.ar", telefono: "3515551234", materiasIds: [1, 2] },
    description: "Solo SUPER_ADMIN. Crea el docente y su usuario; la contraseña inicial es el DNI.",
  }),
  req({
    name: "Actualizar docente",
    method: "PUT",
    path: "/api/docentes/1",
    body: { nombres: "María", apellidos: "González", dni: "28111222", email: "maria.gonzalez@coviello.edu.ar", telefono: "3515551234", materiasIds: [1, 2] },
  }),
  req({ name: "Materias asignadas a un docente", method: "GET", path: "/api/docentes/1/materias" }),
  req({ name: "Asignar materia a docente", method: "POST", path: "/api/docentes/1/materias/1" }),
  req({ name: "Quitar materia de docente", method: "DELETE", path: "/api/docentes/1/materias/1" }),
  req({ name: "Dar de alta/baja a un docente", method: "PATCH", path: "/api/docentes/1/estado", query: [{ key: "activo", value: "true" }], description: "Solo SUPER_ADMIN." }),
]);

// ── 10. Portal Docente ────────────────────────────────────────────
const docentePortalFolder = folder("10. Portal Docente", "Endpoints para el docente autenticado. Requiere rol DOCENTE.", [
  req({ name: "Mis materias", method: "GET", path: "/api/docente-portal/mis-materias" }),
  req({ name: "Mis alumnos", method: "GET", path: "/api/docente-portal/mis-alumnos", description: "Alumnos de las carreras del docente con inscripción CONFIRMADA." }),
  req({ name: "Detalle de un alumno", method: "GET", path: "/api/docente-portal/alumnos/1" }),
]);

// ── 11. Carreras ───────────────────────────────────────────────────
const carrerasFolder = folder("11. Carreras", "Gestión de carreras, años, materias, comisiones. Altas/bajas requieren SUPER_ADMIN.", [
  req({ name: "Listar carreras activas (público)", method: "GET", path: "/api/carreras", auth: noauth }),
  req({ name: "Listar todas las carreras", method: "GET", path: "/api/carreras/todas", description: "ADMIN / SUPER_ADMIN." }),
  req({ name: "Obtener carrera por ID", method: "GET", path: "/api/carreras/1", description: "ALUMNO / ADMIN / SUPER_ADMIN." }),
  req({ name: "Detalle completo (años y materias)", method: "GET", path: "/api/carreras/1/detalle", description: "ADMIN / SUPER_ADMIN." }),
  req({ name: "Crear carrera", method: "POST", path: "/api/carreras", body: { nombre: "Tecnicatura en Programación", descripcion: "Carrera de 3 años orientada a desarrollo de software", activa: true } }),
  req({ name: "Actualizar carrera", method: "PUT", path: "/api/carreras/1", body: { nombre: "Tecnicatura en Programación", descripcion: "Carrera de 3 años orientada a desarrollo de software", activa: true } }),
  req({ name: "Eliminar carrera", method: "DELETE", path: "/api/carreras/1" }),
  req({ name: "Agregar año a la carrera", method: "POST", path: "/api/carreras/1/anios", body: { numeroAnio: 1 } }),
  req({ name: "Eliminar año (y sus materias)", method: "DELETE", path: "/api/carreras/anios/1" }),
  req({
    name: "Agregar materia a un año",
    method: "POST",
    path: "/api/carreras/anios/1/materias",
    body: { nombre: "Programación I", descripcion: "Introducción a la programación", anioCarreraId: 1, docenteId: null, horarios: [] },
  }),
  req({
    name: "Actualizar materia",
    method: "PUT",
    path: "/api/carreras/materias/1",
    body: { nombre: "Programación I", descripcion: "Introducción a la programación", anioCarreraId: 1, docenteId: null, horarios: [] },
  }),
  req({ name: "Eliminar materia", method: "DELETE", path: "/api/carreras/materias/1" }),
  req({ name: "Agregar comisión a un año", method: "POST", path: "/api/carreras/anios/1/comisiones", body: { nombre: "Comisión A", cupoMaximo: 40, prefijoTurno: "A", activa: true } }),
  req({ name: "Actualizar comisión", method: "PUT", path: "/api/carreras/comisiones/1", body: { nombre: "Comisión A", cupoMaximo: 40, prefijoTurno: "A", activa: true } }),
  req({ name: "Eliminar comisión", method: "DELETE", path: "/api/carreras/comisiones/1" }),
  req({ name: "Listar docentes disponibles", method: "GET", path: "/api/carreras/docentes", description: "ADMIN / SUPER_ADMIN. Usado en selector del frontend." }),
]);

// ── 12. Documentos Digitales ──────────────────────────────────────
const documentosFolder = folder("12. Documentos Digitales", "Carga y revisión de documentos digitales de alumnos habilitados.", [
  req({ name: "Listar documentos de un alumno", method: "GET", path: "/api/documentos-digitales/alumno/1", description: "ADMIN / SUPER_ADMIN." }),
  req({
    name: "Registrar URL de documento digital",
    method: "POST",
    path: "/api/documentos-digitales/alumno/1",
    query: [{ key: "tipoDocumento", value: "DNI_FRENTE" }, { key: "archivoUrl", value: "https://storage.ejemplo.com/documentos/dni-frente.pdf" }],
    description: "ALUMNO / ADMIN / SUPER_ADMIN. El alumno registra la URL de un documento subido previamente a almacenamiento externo.",
  }),
  req({ name: "Aprobar documento", method: "PUT", path: "/api/documentos-digitales/1/aprobar", description: "ADMIN / SUPER_ADMIN." }),
  req({
    name: "Rechazar documento",
    method: "PUT",
    path: "/api/documentos-digitales/1/rechazar",
    body: { motivo: "La imagen se ve borrosa, volver a subir." },
    description: "ADMIN / SUPER_ADMIN.",
  }),
  req({ name: "Ver documento por ID", method: "GET", path: "/api/documentos-digitales/1", description: "ADMIN / SUPER_ADMIN / ALUMNO." }),
]);

// ── 13. Turnos ─────────────────────────────────────────────────────
const turnosFolder = folder("13. Turnos", "Gestión de turnos de inscripción presencial.", [
  req({ name: "Días de inscripción disponibles (público)", method: "GET", path: "/api/turnos/dias-disponibles", auth: noauth }),
  req({
    name: "Buscar preinscripción para turno (público)",
    method: "POST",
    path: "/api/turnos/buscar",
    auth: noauth,
    body: { tipoBusqueda: "DNI", valor: "30111222", nombre: null, apellido: null },
    description: "tipoBusqueda: CODIGO | DNI | NOMBRE.",
  }),
  req({
    name: "Solicitar turno (público)",
    method: "POST",
    path: "/api/turnos/solicitar",
    auth: noauth,
    body: { configuracionTurnoId: 1, preinscripcionId: null, tipoBusqueda: "DNI", valor: "30111222", nombre: null, apellido: null },
    description: "El aspirante se identifica por código, DNI o nombre+apellido. Si se envía preinscripcionId, se salta la búsqueda por texto.",
  }),
  req({ name: "Confirmar turno (público)", method: "GET", path: "/api/turnos/confirmar", auth: noauth, query: [{ key: "token", value: "abc123..." }] }),
  req({ name: "Listar configuraciones de días de inscripción", method: "GET", path: "/api/turnos/configuracion", description: "ADMIN / SUPER_ADMIN." }),
  req({
    name: "Crear día de inscripción",
    method: "POST",
    path: "/api/turnos/configuracion",
    body: { nombre: "Inscripción presencial - Marzo", fecha: "2026-03-10", horarioInicio: "09:00:00", horarioFin: "13:00:00", intervaloMinutos: 15, cupoMaximo: 16 },
  }),
  req({
    name: "Actualizar día de inscripción",
    method: "PUT",
    path: "/api/turnos/configuracion/1",
    body: { nombre: "Inscripción presencial - Marzo", fecha: "2026-03-10", horarioInicio: "09:00:00", horarioFin: "13:00:00", intervaloMinutos: 15, cupoMaximo: 16 },
  }),
  req({ name: "Eliminar día de inscripción", method: "DELETE", path: "/api/turnos/configuracion/1" }),
  req({ name: "Asignar turno manualmente", method: "POST", path: "/api/turnos/asignar/1", description: "ADMIN / SUPER_ADMIN." }),
  req({
    name: "Notificar apertura de turnos (masivo)",
    method: "POST",
    path: "/api/turnos/notificar-apertura",
    body: { mensaje: "Ya podés solicitar tu turno de inscripción presencial." },
    description: "ADMIN / SUPER_ADMIN. Notifica a todos los aspirantes pendientes.",
  }),
  req({ name: "Ver turno de una preinscripción", method: "GET", path: "/api/turnos/preinscripcion/1", description: "ADMIN / SUPER_ADMIN / ALUMNO." }),
]);

// ── 14. Calendario Académico ──────────────────────────────────────
const calendarioFolder = folder("14. Calendario Académico", "Feriados, eventos y días no cursables.", [
  req({ name: "Listar eventos por rango (público)", method: "GET", path: "/api/calendario", auth: noauth, query: [{ key: "desde", value: "2026-01-01" }, { key: "hasta", value: "2026-12-31" }] }),
  req({ name: "Listar eventos por tipo (público)", method: "GET", path: "/api/calendario/tipo/FERIADO", auth: noauth, description: "tipo: FERIADO | SUSPENSION | EVENTO." }),
  req({ name: "Días no cursables de una carrera", method: "GET", path: "/api/calendario/no-cursables/1", query: [{ key: "desde", value: "2026-01-01" }, { key: "hasta", value: "2026-12-31" }], description: "ADMIN / SUPER_ADMIN / DOCENTE." }),
  req({
    name: "Crear evento de calendario",
    method: "POST",
    path: "/api/calendario",
    body: { fecha: "2026-10-12", tipo: "FERIADO", descripcion: "Día del Respeto a la Diversidad Cultural", afectaA: "TODAS", carreraId: null },
    description: "Solo SUPER_ADMIN. afectaA: TODAS | CARRERA (con carreraId).",
  }),
  req({ name: "Eliminar evento", method: "DELETE", path: "/api/calendario/1", description: "Solo SUPER_ADMIN." }),
]);

// ── 15. Configuración del Sistema ─────────────────────────────────
const configuracionFolder = folder("15. Configuración del Sistema", "Ajustes globales gestionados por SUPER_ADMIN.", [
  req({ name: "Estado de preinscripción (público)", method: "GET", path: "/api/configuracion/preinscripcion", auth: noauth }),
  req({ name: "Habilitar/deshabilitar preinscripción", method: "PUT", path: "/api/configuracion/preinscripcion", body: { habilitada: true }, description: "Solo SUPER_ADMIN." }),
  req({ name: "Estado de turnos (público)", method: "GET", path: "/api/configuracion/turnos", auth: noauth }),
  req({
    name: "Habilitar/deshabilitar turnos",
    method: "PUT",
    path: "/api/configuracion/turnos",
    body: { habilitado: true },
    description: "Solo SUPER_ADMIN. Al habilitar, envía email masivo a los aspirantes pendientes.",
  }),
]);

// ── 16. Perfil ─────────────────────────────────────────────────────
const perfilFolder = folder("16. Perfil", "Datos del usuario ALUMNO autenticado.", [
  req({ name: "Obtener perfil", method: "GET", path: "/api/perfil" }),
  req({ name: "Actualizar perfil (dirección/teléfono)", method: "PUT", path: "/api/perfil", body: { direccion: "Av. Colón 1234", telefono: "3515556677" }, description: "Solo ALUMNO." }),
  req({ name: "Documentos del alumno autenticado", method: "GET", path: "/api/perfil/documentos" }),
  req({
    name: "Subir documento digital (archivo)",
    method: "POST",
    path: "/api/perfil/documentos/DNI_FRENTE",
    headers: [],
    description: "Solo ALUMNO. multipart/form-data con el campo `archivo`. Configurar el body como form-data en Postman: key=archivo (type File).",
  }),
  req({ name: "Servir archivo de documento (público)", method: "GET", path: "/api/perfil/documentos/archivo/ejemplo.pdf", auth: noauth, description: "Usado en links de email; sin JWT." }),
  req({ name: "Horario semanal del alumno", method: "GET", path: "/api/perfil/horarios", description: "Solo ALUMNO. Materias con inscripción CONFIRMADA." }),
  req({ name: "Resumen de asistencias por materia", method: "GET", path: "/api/perfil/asistencias-resumen", description: "Solo ALUMNO." }),
]);

// ── 17. Períodos de Inscripción ───────────────────────────────────
const periodosFolder = folder("17. Períodos de Inscripción", "Gestión de períodos de preinscripción y reinscripción.", [
  req({ name: "Listar todos los períodos", method: "GET", path: "/api/periodos-inscripcion", description: "ADMIN / SUPER_ADMIN." }),
  req({ name: "Obtener período activo por tipo (público)", method: "GET", path: "/api/periodos-inscripcion/activo", query: [{ key: "tipo", value: "REINSCRIPCION" }], description: "tipo: PREINSCRIPCION | REINSCRIPCION." }),
  req({
    name: "Crear período",
    method: "POST",
    path: "/api/periodos-inscripcion",
    body: { nombre: "Reinscripción 2do cuatrimestre 2026", fechaInicio: "2026-07-01", fechaFin: "2026-07-15", tipo: "REINSCRIPCION" },
    description: "Solo SUPER_ADMIN. Solo puede haber un período activo por tipo a la vez.",
  }),
  req({ name: "Cerrar período activo", method: "PUT", path: "/api/periodos-inscripcion/1/cerrar", description: "Solo SUPER_ADMIN." }),
]);

// ── 18. Permisos ───────────────────────────────────────────────────
const permisosFolder = folder("18. Permisos", "Permisos granulares otorgados a usuarios ADMIN. Todo el módulo requiere SUPER_ADMIN.", [
  req({ name: "Ver permisos activos de un usuario", method: "GET", path: "/api/permisos/usuario/1" }),
  req({
    name: "Otorgar permiso",
    method: "POST",
    path: "/api/permisos",
    body: { usuarioId: 2, codigoPermiso: "HABILITAR_ALUMNO", fechaHasta: null },
    description: "codigoPermiso: HABILITAR_ALUMNO | REGISTRAR_PAGO | GESTIONAR_DOCUMENTOS | ENVIAR_MAILS | GESTIONAR_CARRERAS | GESTIONAR_MATERIAS | GESTIONAR_DOCENTES. Si fechaHasta es null, no expira.",
  }),
  req({ name: "Revocar permiso", method: "DELETE", path: "/api/permisos/1", description: "Fija fechaHasta = ahora; el permiso queda inactivo inmediatamente." }),
]);

// ── 19. Envío de Emails ───────────────────────────────────────────
const mailsFolder = folder("19. Envío de Emails", "Envío masivo de emails a alumnos, con historial. ADMIN / SUPER_ADMIN.", [
  req({
    name: "Enviar email masivo",
    method: "POST",
    path: "/api/mails/enviar",
    body: { asunto: "Recordatorio inscripción", cuerpo: "Les recordamos que...", destinatarioTipo: "POR_CARRERA", carreraId: 1, anioCarreraId: null },
    description: "destinatarioTipo: TODOS | POR_CARRERA | POR_ANIO | DOCS_FALTANTES.",
  }),
  req({ name: "Ver historial de envíos", method: "GET", path: "/api/mails/historial", query: [{ key: "page", value: 0 }, { key: "size", value: 20 }] }),
]);

// ── 20. Gestión de Usuarios Admin ─────────────────────────────────
const usuariosAdminFolder = folder("20. Gestión de Usuarios Admin", "El SUPER_ADMIN crea y gestiona cuentas ADMIN y DOCENTE.", [
  req({ name: "Listar administradores", method: "GET", path: "/api/admin/usuarios" }),
  req({
    name: "Crear usuario ADMIN o DOCENTE",
    method: "POST",
    path: "/api/admin/usuarios",
    body: { nombres: "Ana", apellidos: "Rodríguez", email: "ana.rodriguez@coviello.edu.ar", rol: "ADMIN" },
    description: "rol: ADMIN | DOCENTE. Genera contraseña aleatoria y la envía por email.",
  }),
  req({ name: "Desactivar usuario ADMIN o DOCENTE", method: "DELETE", path: "/api/admin/usuarios/1", description: "No se puede desactivar al propio SUPER_ADMIN." }),
]);

const collection = {
  info: {
    _postman_id: "8f2b6b3a-4c1a-4e9a-9a3a-6b1e2d0a9c10",
    name: "Gestión de Alumnos - IES Coviello API",
    description: "Colección completa de endpoints del backend de Gestión de Alumnos (IES Alfredo Coviello). " +
      "Autenticación: JWT Bearer, variable de colección {{token}}. Base URL: variable {{baseUrl}} (default http://localhost:8080). " +
      "Los endpoints públicos (formulario de preinscripción, turnos, carreras, Arduino, etc.) tienen auth = No Auth explícito.",
    schema: "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
  },
  auth: {
    type: "bearer",
    bearer: [{ key: "token", value: "{{token}}", type: "string" }],
  },
  event: [],
  variable: [
    { key: "baseUrl", value: "http://localhost:8080", type: "string" },
    { key: "token", value: "", type: "string" },
  ],
  item: [
    authFolder,
    preinscripcionesFolder,
    alumnosFolder,
    arduinoFolder,
    asistenciasFolder,
    materiasFolder,
    inscripcionesMateriaFolder,
    horariosFolder,
    docentesFolder,
    docentePortalFolder,
    carrerasFolder,
    documentosFolder,
    turnosFolder,
    calendarioFolder,
    configuracionFolder,
    perfilFolder,
    periodosFolder,
    permisosFolder,
    mailsFolder,
    usuariosAdminFolder,
  ],
};

const outPath = path.join(__dirname, "GestionAlumnos.postman_collection.json");
fs.writeFileSync(outPath, JSON.stringify(collection, null, 2), "utf-8");

let total = 0;
for (const f of collection.item) total += f.item.length;
console.log(`Colección generada: ${outPath}`);
console.log(`Carpetas: ${collection.item.length} | Endpoints: ${total}`);
