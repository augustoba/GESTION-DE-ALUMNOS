package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.PreinscripcionDetalleResponse;
import coviello.gestion_de_alumnos.dto.AprobarRequest;
import coviello.gestion_de_alumnos.dto.PreinscripcionRequest;
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
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN')")
    @Operation(
        summary = "Crear preinscripción (ALUMNO)",
        description = """
            El alumno completa el formulario de preinscripción con sus datos personales.
            El sistema guarda el formulario, le asigna un ID único y envía el formulario
            en formato PDF al email del alumno para que lo imprima y lo lleve el día de
            la inscripción presencial junto con la documentación requerida.
            Estado inicial: ENVIADA.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Formulario enviado. Se envió el PDF al email del alumno."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o sin cupos disponibles para la carrera seleccionada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos para realizar esta acción.")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "nombres": "Juan",
                  "apellidos": "Pérez",
                  "dni": "12345678",
                  "fechaNacimiento": "1995-08-20",
                  "lugarNacimiento": "Córdoba",
                  "nacionalidad": "Argentina",
                  "domicilio": "Av. Corrientes 1234",
                  "localidad": "Buenos Aires",
                  "telefono": "1123456789",
                  "email": "juan.perez@gmail.com",
                  "egresadoDe": "Colegio Nacional N°1",
                  "tituloDe": "Bachiller con orientación en Informática",
                  "debeMaterias": false,
                  "materiasAdeudadas": null,
                  "afeccionEspecifica": null,
                  "grupoSanguineo": "A+",
                  "carreraId": 1
                }
                """)
        )
    )
    public ResponseEntity<ApiResponse> crearPreinscripcion(@Valid @RequestBody PreinscripcionRequest request) {
        Preinscripcion preinscripcion = new Preinscripcion();
        preinscripcion.setNombre(request.nombres());
        preinscripcion.setApellido(request.apellidos());
        preinscripcion.setDni(request.dni());
        preinscripcion.setFechaNacimiento(request.fechaNacimiento());
        preinscripcion.setLugarNacimiento(request.lugarNacimiento());
        preinscripcion.setNacionalidad(request.nacionalidad());
        preinscripcion.setDireccion(request.domicilio());
        preinscripcion.setLocalidad(request.localidad());
        preinscripcion.setTelefono(request.telefono());
        preinscripcion.setEmail(request.email());
        preinscripcion.setEgresadoDe(request.egresadoDe());
        preinscripcion.setTituloDe(request.tituloDe());
        preinscripcion.setDebeMaterias(request.debeMaterias());
        preinscripcion.setMateriasAdeudadas(request.materiasAdeudadas());
        preinscripcion.setAfeccionEspecifica(request.afeccionEspecifica());
        preinscripcion.setGrupoSanguineo(request.grupoSanguineo());

        if (request.carreraId() != null) {
            Carrera carrera = carreraService.obtenerPorId(request.carreraId());
            preinscripcion.setCarrera(carrera);
        }

        Preinscripcion guardada = preinscripcionService.guardar(preinscripcion);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(
                        "Formulario de preinscripción N° " + guardada.getId() +
                        " registrado. Se envió el PDF a tu email para que lo imprimas.",
                        guardada));
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

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ver PDF del formulario de preinscripción (ADMIN)", description = "Devuelve el PDF generado del formulario para visualizarlo en el navegador.")
    public ResponseEntity<byte[]> verPdf(
            @Parameter(description = "ID de la preinscripción", example = "1")
            @PathVariable Long id) {

        byte[] pdf = preinscripcionService.generarPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "formulario-preinscripcion-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
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

    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Aprobar preinscripción presencial (ADMIN)",
        description = """
            El alumno se presentó presencialmente con el formulario impreso y la documentación.
            El admin busca el formulario por su ID, marca en el cuerpo cuáles documentos físicos
            presentó el alumno, y confirma la aprobación.
            El alumno queda dado de alta en el sistema (status = true) y recibe un email de bienvenida.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Alumno dado de alta. Email de bienvenida enviado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Preinscripción no encontrada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "tituloSecundario": true,
                  "constanciaTituloTramite": false,
                  "dni": true,
                  "foto": true,
                  "actaNacimiento": true,
                  "psicofisico": true,
                  "buenaConducta": true
                }
                """)
        )
    )
    public ResponseEntity<ApiResponse> aprobar(
            @Parameter(description = "ID del formulario de preinscripción", example = "1")
            @PathVariable Long id,
            @RequestBody AprobarRequest requisitos) {

        Preinscripcion pre = preinscripcionService.aprobar(id, requisitos);
        return ResponseEntity.ok(new ApiResponse(
                "Alumno dado de alta correctamente. Se notificó por email.",
                pre));
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
