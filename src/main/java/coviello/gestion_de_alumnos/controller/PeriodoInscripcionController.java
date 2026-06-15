package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.PeriodoInscripcionRequest;
import coviello.gestion_de_alumnos.model.TipoPeriodoInscripcion;
import coviello.gestion_de_alumnos.service.PeriodoInscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/periodos-inscripcion")
@Tag(name = "Períodos de Inscripción", description = "Gestión de períodos de preinscripción y reinscripción.")
public class PeriodoInscripcionController {

    private final PeriodoInscripcionService periodoService;

    public PeriodoInscripcionController(PeriodoInscripcionService periodoService) {
        this.periodoService = periodoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Listar todos los períodos (ADMIN)")
    public ResponseEntity<ApiResponse> listar() {
        return ResponseEntity.ok(new ApiResponse("Períodos de inscripción", periodoService.listarTodos()));
    }

    @GetMapping("/activo")
    @Operation(summary = "Obtener período activo por tipo (público)",
               description = "tipo: PREINSCRIPCION o REINSCRIPCION")
    public ResponseEntity<ApiResponse> activo(@RequestParam TipoPeriodoInscripcion tipo) {
        return ResponseEntity.ok(new ApiResponse("Período activo", periodoService.obtenerActivo(tipo)));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Crear nuevo período (SUPER_ADMIN)",
               description = "Solo puede haber un período activo por tipo a la vez.")
    public ResponseEntity<ApiResponse> crear(@RequestBody PeriodoInscripcionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Período creado", periodoService.crear(req)));
    }

    @PutMapping("/{id}/cerrar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Cerrar período activo (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> cerrar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Período cerrado", periodoService.cerrar(id)));
    }
}
