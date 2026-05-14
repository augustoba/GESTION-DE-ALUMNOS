package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.PreinscripcionDetalleResponse;
import coviello.gestion_de_alumnos.dto.RevisionDocumentosRequest;
import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.model.Preinscripcion;
import coviello.gestion_de_alumnos.service.CarreraService;
import coviello.gestion_de_alumnos.service.PreinscripcionService;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/preinscripciones")
@Tag(name = "Preinscripciones", description = "Gestión del proceso de inscripción: creación, validación de pago y seguimiento de estado.")
public class PreinscripcionController {

    private final PreinscripcionService preinscripcionService;
    private final CarreraService carreraService;

    public PreinscripcionController(PreinscripcionService preinscripcionService, CarreraService carreraService) {
        this.preinscripcionService = preinscripcionService;
        this.carreraService = carreraService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN')")
    @Operation(
        summary = "Crear preinscripción (ALUMNO)",
        description = """
            El alumno completa su preinscripción en un solo paso: datos personales, selección de carrera,
            comprobante de pago y los cuatro documentos obligatorios (DNI frente, DNI dorso, título secundario y foto carnet).
            Estado inicial: PENDIENTE_PAGO. El admin revisará el pago y cada documento por separado.
            Si la carrera llegó al cupo máximo, la inscripción es rechazada.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Preinscripción creada. El admin revisará el pago y los documentos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "No hay cupos disponibles para la carrera seleccionada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos para realizar esta acción.")
    })
    public ResponseEntity<ApiResponse> crearPreinscripcion(
            @Parameter(description = "Nombre del alumno", example = "Juan") @RequestParam String nombre,
            @Parameter(description = "Apellido del alumno", example = "Pérez") @RequestParam String apellido,
            @Parameter(description = "DNI del alumno (sin puntos)", example = "12345678") @RequestParam String dni,
            @Parameter(description = "Email del alumno", example = "juan.perez@gmail.com") @RequestParam String email,
            @Parameter(description = "Teléfono de contacto", example = "1123456789") @RequestParam String telefono,
            @Parameter(description = "Dirección (calle y número)", example = "Av. Corrientes 1234") @RequestParam String direccion,
            @Parameter(description = "Fecha de nacimiento (YYYY-MM-DD)", example = "1995-08-20") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
            @Parameter(description = "ID de la carrera", example = "1") @RequestParam Long carreraId,
            @Parameter(description = "Comprobante de pago (JPG, PNG o PDF)") @RequestParam("comprobante") MultipartFile comprobante,
            @Parameter(description = "Foto del frente del DNI (JPG o PNG)") @RequestParam("dniFente") MultipartFile dniFente,
            @Parameter(description = "Foto del dorso del DNI (JPG o PNG)") @RequestParam("dniDorso") MultipartFile dniDorso,
            @Parameter(description = "PDF del título secundario") @RequestParam("titulo") MultipartFile titulo,
            @Parameter(description = "Foto carnet (JPG o PNG, fondo blanco)") @RequestParam("fotoCarnet") MultipartFile fotoCarnet) {

        try {
            Carrera carrera = carreraService.obtenerPorId(carreraId);

            Preinscripcion preinscripcion = new Preinscripcion();
            preinscripcion.setNombre(nombre);
            preinscripcion.setApellido(apellido);
            preinscripcion.setDni(dni);
            preinscripcion.setEmail(email);
            preinscripcion.setTelefono(telefono);
            preinscripcion.setDireccion(direccion);
            preinscripcion.setFechaNacimiento(fechaNacimiento);
            preinscripcion.setCarrera(carrera);

            Preinscripcion guardada = preinscripcionService.guardar(
                    preinscripcion, comprobante, dniFente, dniDorso, titulo, fotoCarnet);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Preinscripción creada. El equipo de administración revisará tu documentación.", guardada));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Error al guardar los archivos: " + e.getMessage(), null));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar todas las preinscripciones paginadas (ADMIN)",
        description = """
            Devuelve todas las preinscripciones ordenadas de la más antigua a la más nueva (por fechaCreacion ASC).
            Soporta paginado mediante los parámetros 'page' (número de página, desde 0) y 'size' (cantidad por página, por defecto 20).
            La respuesta incluye metadatos de paginación: totalElements, totalPages, number, size.
            No incluye los archivos binarios (comprobante de pago ni documentos).
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista devuelta correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> obtenerTodas(
            @Parameter(description = "Número de página (empieza en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de resultados por página", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        Page<Preinscripcion> resultado = preinscripcionService.obtenerTodasPaginadas(page, size);
        return ResponseEntity.ok(new ApiResponse("Listado de preinscripciones", resultado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Obtener detalle completo de una preinscripción (ADMIN)",
        description = """
            Devuelve todos los datos del alumno (nombre, apellido, DNI, email, teléfono, dirección, fecha de nacimiento)
            junto con el estado general de la preinscripción y la lista completa de documentos subidos.
            Cada documento incluye su tipo (DNI_FRENTE, DNI_DORSO, TITULO, FOTO_CARNET),
            nombre de archivo, tipo de contenido y estado de validación (PENDIENTE, VALIDADO o RESUBIR).
            Desde esta vista el admin puede validar o pedir la resubida de cada documento individualmente
            usando los endpoints PUT /api/documentos/{id}/validar y PUT /api/documentos/{id}/resubir.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Detalle devuelto correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Preinscripción no encontrada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> obtenerDetalle(
            @Parameter(description = "ID de la preinscripción", example = "1")
            @PathVariable Long id) {

        PreinscripcionDetalleResponse detalle = preinscripcionService.obtenerDetalle(id);
        return ResponseEntity.ok(new ApiResponse("Detalle de la preinscripción", detalle));
    }

    @GetMapping("/pendientes-pago")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar pendientes de validación de pago (ADMIN)",
        description = "Devuelve solo las preinscripciones en estado PENDIENTE_PAGO. Estas son las que el admin debe revisar y aprobar o rechazar."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista devuelta correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> obtenerPendientesPago() {
        List<Preinscripcion> lista = preinscripcionService.obtenerPendientesPago();
        return ResponseEntity.ok(new ApiResponse("Preinscripciones pendientes de validación de pago", lista));
    }

    @PutMapping("/{id}/validar-pago")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Validar comprobante de pago (ADMIN)",
        description = """
            El admin confirma que el comprobante de pago es válido.
            El estado pasa a PAGO_VALIDADO y se le envía un email al alumno indicando que debe subir la documentación:
            DNI frente, DNI dorso y título secundario.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pago validado. Email enviado al alumno con instrucciones para subir documentos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Preinscripción no encontrada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> validarPago(
            @Parameter(description = "ID de la preinscripción a validar", example = "1")
            @PathVariable Long id) {

        Preinscripcion pre = preinscripcionService.validarPago(id);
        return ResponseEntity.ok(new ApiResponse("Pago validado. Se notificó al alumno por email.", pre));
    }

    @GetMapping("/con-documentos-pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Preinscripciones con documentos pendientes de revisión (ADMIN)",
        description = "Devuelve las preinscripciones que tienen al menos un documento en estado PENDIENTE (subido por el alumno pero aún no revisado por el admin)."
    )
    public ResponseEntity<ApiResponse> conDocumentosPendientes() {
        List<Preinscripcion> lista = preinscripcionService.obtenerConDocumentosPendientes();
        return ResponseEntity.ok(new ApiResponse("Preinscripciones con documentos pendientes de revisión", lista));
    }

    @GetMapping("/con-documentos-rechazados")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Preinscripciones con documentos rechazados (ADMIN)",
        description = "Devuelve las preinscripciones que tienen al menos un documento en estado RESUBIR (rechazado por el admin, el alumno debe volver a subirlo)."
    )
    public ResponseEntity<ApiResponse> conDocumentosRechazados() {
        List<Preinscripcion> lista = preinscripcionService.obtenerConDocumentosRechazados();
        return ResponseEntity.ok(new ApiResponse("Preinscripciones con documentos rechazados", lista));
    }

    @GetMapping("/con-documentos-faltantes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Preinscripciones con documentos faltantes (ADMIN)",
        description = "Devuelve las preinscripciones (con pago validado) donde el alumno no subió los cuatro documentos obligatorios: DNI frente, DNI dorso, título y foto carnet."
    )
    public ResponseEntity<ApiResponse> conDocumentosFaltantes() {
        List<Preinscripcion> lista = preinscripcionService.obtenerConDocumentosFaltantes();
        return ResponseEntity.ok(new ApiResponse("Preinscripciones con documentos faltantes", lista));
    }

    @PutMapping("/{id}/confirmar-revision")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Confirmar revisión de documentos (ADMIN)",
        description = """
            El admin envía la decisión final para cada documento (VALIDADO o RESUBIR).
            Si todos los documentos obligatorios quedan VALIDADOS:
              - La preinscripción pasa a estado APROBADA.
              - El alumno recibe acceso al sistema (status = true).
              - Se le envía un email informando que su inscripción fue aprobada.
            Si algún documento queda en RESUBIR:
              - Se le envía un email al alumno con la lista de documentos que debe volver a subir.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Revisión confirmada. Email enviado al alumno."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Preinscripción o documento no encontrado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "decisiones": [
                    { "documentoId": 1, "estado": "VALIDADO" },
                    { "documentoId": 2, "estado": "VALIDADO" },
                    { "documentoId": 3, "estado": "RESUBIR" },
                    { "documentoId": 4, "estado": "VALIDADO" }
                  ]
                }
                """)
        )
    )
    public ResponseEntity<ApiResponse> confirmarRevision(
            @Parameter(description = "ID de la preinscripción", example = "1")
            @PathVariable Long id,
            @RequestBody RevisionDocumentosRequest request) {

        Preinscripcion pre = preinscripcionService.confirmarRevision(id, request);
        String mensaje = pre.getEstado() == coviello.gestion_de_alumnos.model.EstadoPreinscripcion.APROBADA
                ? "Inscripción aprobada. Se notificó al alumno por email."
                : "Revisión guardada. Se notificó al alumno sobre los documentos a corregir.";
        return ResponseEntity.ok(new ApiResponse(mensaje, pre));
    }

    @PutMapping("/{id}/rechazar-pago")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Rechazar comprobante de pago (ADMIN)",
        description = """
            El admin rechaza el comprobante porque es ilegible, el monto no coincide u otro motivo.
            Se le envía un email al alumno con el motivo del rechazo para que suba un nuevo comprobante.
            La preinscripción vuelve a PENDIENTE_PAGO. El contador de 48hs sigue desde la creación original.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pago rechazado. Email enviado al alumno con el motivo."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Preinscripción no encontrada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "motivo": "El comprobante es ilegible. Por favor subí una imagen más clara."
                }
                """)
        )
    )
    public ResponseEntity<ApiResponse> rechazarPago(
            @Parameter(description = "ID de la preinscripción", example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String motivo = body.getOrDefault("motivo", "Comprobante inválido o ilegible");
        Preinscripcion pre = preinscripcionService.rechazarPago(id, motivo);
        return ResponseEntity.ok(new ApiResponse("Pago rechazado. Se notificó al alumno por email.", pre));
    }
}
