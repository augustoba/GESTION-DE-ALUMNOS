package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.AsistenciaModificarRequest;
import coviello.gestion_de_alumnos.dto.AsistenciaRegistroRequest;
import coviello.gestion_de_alumnos.service.AsistenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/asistencias")
@Tag(name = "Asistencias", description = "Registro de asistencia vía Arduino o manual, y consultas de regularidad.")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PostMapping("/arduino")
    @Operation(summary = "Registrar asistencia desde Arduino (sistema)",
               description = "El Arduino envía identificadorArduino y alumnoId (HUELLA) o pin (PIN).")
    public ResponseEntity<ApiResponse> registrarDesdeArduino(@RequestBody AsistenciaRegistroRequest req) {
        return ResponseEntity.ok(new ApiResponse("Asistencia registrada",
                asistenciaService.registrarDesdeArduino(req)));
    }

    @GetMapping("/horario/{horarioClaseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar asistencias de un horario en una fecha (ADMIN/DOCENTE)")
    public ResponseEntity<ApiResponse> listarPorHorarioYFecha(
            @PathVariable Long horarioClaseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(new ApiResponse("Asistencias del día",
                asistenciaService.listarPorHorarioYFecha(horarioClaseId, fecha)));
    }

    @GetMapping("/resumen/{alumnoId}/{materiaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE', 'ALUMNO')")
    @Operation(summary = "Resumen de asistencia de un alumno en una materia",
               description = "Devuelve totales, porcentaje y flag 'libre' (por debajo del mínimo configurado).")
    public ResponseEntity<ApiResponse> resumen(
            @PathVariable Long alumnoId,
            @PathVariable Long materiaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(new ApiResponse("Resumen de asistencia",
                asistenciaService.resumenAlumnoMateria(alumnoId, materiaId, desde, hasta)));
    }

    @GetMapping("/calendario/{materiaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Vista calendario de asistencias de una materia (DOCENTE)",
               description = "Devuelve un mapa fecha → lista de asistencias para el calendario del docente.")
    public ResponseEntity<ApiResponse> vistaCalendario(
            @PathVariable Long materiaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(new ApiResponse("Calendario de asistencias",
                asistenciaService.vistaCalendario(materiaId, desde, hasta)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Modificar asistencia manualmente (ADMIN/DOCENTE)",
               description = "Permite corregir el estado y añadir justificación. El método queda como MANUAL.")
    public ResponseEntity<ApiResponse> modificar(@PathVariable Long id,
                                                  @RequestBody AsistenciaModificarRequest req,
                                                  Authentication auth) {
        return ResponseEntity.ok(new ApiResponse("Asistencia modificada",
                asistenciaService.modificar(id, req, auth.getName())));
    }
}
