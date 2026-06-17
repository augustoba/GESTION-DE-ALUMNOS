package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.MateriaRequest;
import coviello.gestion_de_alumnos.service.MateriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/materias")
@Tag(name = "Materias", description = "ABM de materias y asignación de horarios y docentes.")
public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar todas las materias")
    public ResponseEntity<ApiResponse> listar() {
        return ResponseEntity.ok(new ApiResponse("Materias", materiaService.listarTodas()));
    }

    @GetMapping("/anio-carrera/{anioCarreraId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar materias de un año de carrera específico")
    public ResponseEntity<ApiResponse> listarPorAnio(@PathVariable Long anioCarreraId) {
        return ResponseEntity.ok(new ApiResponse("Materias del año",
                materiaService.listarPorAnioCarrera(anioCarreraId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Obtener detalle de una materia")
    public ResponseEntity<ApiResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Materia", materiaService.obtenerPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Crear materia",
               description = "Crea una materia con sus horarios opcionales y docente opcional.")
    public ResponseEntity<ApiResponse> crear(@Valid @RequestBody MateriaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Materia creada", materiaService.crear(req)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Actualizar materia",
               description = "Actualiza datos y horarios. Si se envía lista de horarios vacía se eliminan todos.")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long id,
                                                   @Valid @RequestBody MateriaRequest req) {
        return ResponseEntity.ok(new ApiResponse("Materia actualizada", materiaService.actualizar(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Eliminar materia")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        materiaService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse("Materia eliminada", null));
    }

    @PutMapping("/{id}/docente/{docenteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Asignar docente a una materia")
    public ResponseEntity<ApiResponse> asignarDocente(@PathVariable Long id,
                                                       @PathVariable Long docenteId) {
        return ResponseEntity.ok(new ApiResponse("Docente asignado",
                materiaService.asignarDocente(id, docenteId)));
    }

    @DeleteMapping("/{id}/docente")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Desasignar docente de una materia")
    public ResponseEntity<ApiResponse> desasignarDocente(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Docente desasignado",
                materiaService.desasignarDocente(id)));
    }
}
