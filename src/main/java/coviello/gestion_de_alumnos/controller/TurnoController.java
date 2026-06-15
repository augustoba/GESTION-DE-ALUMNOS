package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.ConfiguracionTurnoRequest;
import coviello.gestion_de_alumnos.dto.SolicitarTurnoRequest;
import coviello.gestion_de_alumnos.dto.PreinscripcionResumenTurno;
import coviello.gestion_de_alumnos.service.TurnoService;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/turnos")
@Tag(name = "Turnos", description = "Gestión de turnos de inscripción presencial.")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    // ── Públicos (sin login) ───────────────────────────────────────

    @GetMapping("/dias-disponibles")
    @Operation(summary = "Listar días de inscripción activos con cupo disponible (público)")
    public ResponseEntity<ApiResponse> diasDisponibles() {
        return ResponseEntity.ok(new ApiResponse("Días de inscripción", turnoService.listarDiasDisponibles()));
    }

    @PostMapping("/buscar")
    @Operation(summary = "Buscar preinscripción por código parcial, DNI o nombre (público)")
    public ResponseEntity<ApiResponse> buscarPreinscripcion(@RequestBody Map<String, String> body) {
        try {
            List<PreinscripcionResumenTurno> resultados = turnoService.buscarParaTurno(
                    body.get("tipoBusqueda"),
                    body.get("valor"),
                    body.get("nombre"),
                    body.get("apellido"));
            if (resultados.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("No se encontraron coincidencias con los datos ingresados.", null));
            }
            return ResponseEntity.ok(new ApiResponse("Resultados encontrados", resultados));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/solicitar")
    @Operation(summary = "Solicitar turno como aspirante (público)",
               description = "El aspirante se identifica con código formulario, DNI o nombre+apellido y elige el día.")
    public ResponseEntity<ApiResponse> solicitar(@RequestBody SolicitarTurnoRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Turno solicitado. Te enviamos la confirmación por email.",
                            turnoService.solicitarTurno(req)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/confirmar")
    @Operation(summary = "Confirmar turno por link de email (público)")
    public ResponseEntity<ApiResponse> confirmar(@RequestParam String token) {
        try {
            return ResponseEntity.ok(new ApiResponse("Turno confirmado exitosamente",
                    turnoService.confirmarTurno(token)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    // ── Admin ──────────────────────────────────────────────────────

    @GetMapping("/configuracion")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Listar todos los días de inscripción configurados (ADMIN)")
    public ResponseEntity<ApiResponse> listarConfiguraciones() {
        return ResponseEntity.ok(new ApiResponse("Días de inscripción", turnoService.listarTodasConfiguraciones()));
    }

    @PostMapping("/configuracion")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Crear día de inscripción (ADMIN)", description = "Define fecha, horario, intervalo y cupo máximo.")
    public ResponseEntity<ApiResponse> crearConfiguracion(@RequestBody ConfiguracionTurnoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Día de inscripción creado", turnoService.crearConfiguracion(req)));
    }

    @PutMapping("/configuracion/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Actualizar un día de inscripción (ADMIN)")
    public ResponseEntity<ApiResponse> actualizarConfiguracion(@PathVariable Long id,
                                                                @RequestBody ConfiguracionTurnoRequest req) {
        return ResponseEntity.ok(new ApiResponse("Día actualizado", turnoService.actualizarConfiguracion(id, req)));
    }

    @DeleteMapping("/configuracion/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Eliminar un día de inscripción (ADMIN)")
    public ResponseEntity<ApiResponse> eliminarConfiguracion(@PathVariable Long id) {
        turnoService.eliminarConfiguracion(id);
        return ResponseEntity.ok(new ApiResponse("Día eliminado", null));
    }

    @PostMapping("/asignar/{preinscripcionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Asignar turno manualmente a una preinscripción (ADMIN)")
    public ResponseEntity<ApiResponse> asignar(@PathVariable Long preinscripcionId) {
        try {
            return ResponseEntity.ok(new ApiResponse("Turno asignado. Email enviado al aspirante.",
                    turnoService.asignarTurno(preinscripcionId)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/notificar-apertura")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Enviar email masivo a todos los preinscriptos (ADMIN)",
               description = "Notifica a todos los aspirantes pendientes que ya pueden solicitar turno.")
    public ResponseEntity<ApiResponse> notificarApertura(@RequestBody Map<String, String> body) {
        String mensaje = body.getOrDefault("mensaje", "");
        int enviados = turnoService.enviarNotificacionMasiva(mensaje);
        return ResponseEntity.ok(new ApiResponse("Notificación enviada a " + enviados + " aspirantes.", null));
    }

    @GetMapping("/preinscripcion/{preinscripcionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ALUMNO')")
    @Operation(summary = "Ver turno de una preinscripción")
    public ResponseEntity<ApiResponse> verTurno(@PathVariable Long preinscripcionId) {
        try {
            return ResponseEntity.ok(new ApiResponse("Turno asignado",
                    turnoService.obtenerPorPreinscripcion(preinscripcionId)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }
}
