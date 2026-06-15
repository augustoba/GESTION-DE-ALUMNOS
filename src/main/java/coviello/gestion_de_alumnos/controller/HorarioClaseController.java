package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.HorarioClaseRequest;
import coviello.gestion_de_alumnos.service.HorarioClaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/horarios")
@Tag(name = "Horarios de Clase", description = "CRUD de horarios de cursada por materia.")
public class HorarioClaseController {

    private final HorarioClaseService horarioClaseService;

    public HorarioClaseController(HorarioClaseService horarioClaseService) {
        this.horarioClaseService = horarioClaseService;
    }

    @GetMapping("/materia/{materiaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar horarios de una materia (ADMIN/DOCENTE)")
    public ResponseEntity<ApiResponse> listarPorMateria(@PathVariable Long materiaId) {
        return ResponseEntity.ok(new ApiResponse("Horarios de la materia",
                horarioClaseService.listarPorMateria(materiaId)));
    }

    @GetMapping("/docente/{docenteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar horarios de un docente (ADMIN/DOCENTE)")
    public ResponseEntity<ApiResponse> listarPorDocente(@PathVariable Long docenteId) {
        return ResponseEntity.ok(new ApiResponse("Horarios del docente",
                horarioClaseService.listarPorDocente(docenteId)));
    }

    @PostMapping("/materia/{materiaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Crear horario para una materia (ADMIN)")
    public ResponseEntity<ApiResponse> crear(@PathVariable Long materiaId,
                                              @RequestBody HorarioClaseRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Horario creado",
                        horarioClaseService.crear(materiaId, req)));
    }

    @PutMapping("/{horarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Actualizar horario (ADMIN)")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long horarioId,
                                                   @RequestBody HorarioClaseRequest req) {
        return ResponseEntity.ok(new ApiResponse("Horario actualizado",
                horarioClaseService.actualizar(horarioId, req)));
    }

    @DeleteMapping("/{horarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Eliminar horario (ADMIN)")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long horarioId) {
        horarioClaseService.eliminar(horarioId);
        return ResponseEntity.ok(new ApiResponse("Horario eliminado", null));
    }
}
