package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.InscripcionMateriaRequest;
import coviello.gestion_de_alumnos.model.EstadoInscripcionMateria;
import coviello.gestion_de_alumnos.service.InscripcionMateriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inscripciones-materia")
@Tag(name = "Inscripciones a Materias", description = "Solicitudes de reinscripción del alumno y resolución por admin/docente.")
public class InscripcionMateriaController {

    private final InscripcionMateriaService inscripcionService;

    public InscripcionMateriaController(InscripcionMateriaService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping("/alumno/{alumnoId}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Solicitar inscripción a materias (ALUMNO)",
               description = "Requiere período REINSCRIPCION activo. Acepta lista de materiaIds.")
    public ResponseEntity<ApiResponse> solicitar(@PathVariable Long alumnoId,
                                                  @RequestBody InscripcionMateriaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Solicitudes de inscripción creadas",
                        inscripcionService.solicitarInscripcion(alumnoId, req)));
    }

    @GetMapping("/alumno/{alumnoId}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Ver inscripciones de un alumno")
    public ResponseEntity<ApiResponse> porAlumno(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(new ApiResponse("Inscripciones del alumno",
                inscripcionService.listarPorAlumno(alumnoId)));
    }

    @GetMapping("/materia/{materiaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Ver inscripciones de una materia (ADMIN/DOCENTE)",
               description = "Filtrar por estado: SOLICITADA, CONFIRMADA, RECHAZADA")
    public ResponseEntity<ApiResponse> porMateria(
            @PathVariable Long materiaId,
            @RequestParam(required = false) EstadoInscripcionMateria estado) {
        EstadoInscripcionMateria filtro = estado != null ? estado : EstadoInscripcionMateria.SOLICITADA;
        return ResponseEntity.ok(new ApiResponse("Inscripciones de la materia",
                inscripcionService.listarPorMateria(materiaId, filtro)));
    }

    @PutMapping("/{id}/resolver")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Confirmar o rechazar solicitud (ADMIN/DOCENTE)")
    public ResponseEntity<ApiResponse> resolver(@PathVariable Long id,
                                                 @RequestBody Map<String, String> body,
                                                 Authentication auth) {
        EstadoInscripcionMateria nuevoEstado = EstadoInscripcionMateria.valueOf(
                body.getOrDefault("estado", "CONFIRMADA").toUpperCase());
        return ResponseEntity.ok(new ApiResponse("Inscripción resuelta",
                inscripcionService.resolverInscripcion(id, nuevoEstado, auth.getName())));
    }
}
