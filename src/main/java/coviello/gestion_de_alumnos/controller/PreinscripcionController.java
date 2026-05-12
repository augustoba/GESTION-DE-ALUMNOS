package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.model.Preinscripcion;
import coviello.gestion_de_alumnos.service.CarreraService;
import coviello.gestion_de_alumnos.service.PreinscripcionService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            El alumno inicia su proceso de inscripción cargando sus datos y el comprobante de pago (foto o captura de transferencia).
            Estado inicial: PENDIENTE_PAGO.
            El alumno tiene 48hs para que el admin valide el pago; si no, la inscripción expira y el cupo se libera.
            Si la carrera llegó al cupo máximo, la inscripción es rechazada.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Preinscripción creada. El admin validará el pago en las próximas horas."),
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
            @Parameter(description = "ID de la carrera a la que se inscribe", example = "1") @RequestParam Long carreraId,
            @Parameter(description = "Captura o foto del comprobante de transferencia (JPG, PNG o PDF)") @RequestParam("comprobante") MultipartFile comprobante) {

        try {
            Carrera carrera = carreraService.obtenerPorId(carreraId);

            Preinscripcion preinscripcion = new Preinscripcion();
            preinscripcion.setNombre(nombre);
            preinscripcion.setApellido(apellido);
            preinscripcion.setDni(dni);
            preinscripcion.setEmail(email);
            preinscripcion.setTelefono(telefono);
            preinscripcion.setCarrera(carrera);

            Preinscripcion guardada = preinscripcionService.guardar(preinscripcion, comprobante);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Preinscripción creada. Tenés 48hs para completar la validación del pago.", guardada));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Error al guardar el comprobante: " + e.getMessage(), null));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar todas las preinscripciones (ADMIN)",
        description = "Devuelve todas las preinscripciones sin importar su estado (PENDIENTE_PAGO, PAGO_VALIDADO, DOCUMENTOS_COMPLETOS, EXPIRADA)."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista devuelta correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> obtenerTodas() {
        List<Preinscripcion> lista = preinscripcionService.obtenerTodas();
        return ResponseEntity.ok(new ApiResponse("Listado de preinscripciones", lista));
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
