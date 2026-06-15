package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alumnos")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Alumnos", description = "Gestión de alumnos habilitados. Requiere rol ADMIN.")
public class AlumnoController {

    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los alumnos paginados")
    public ResponseEntity<ApiResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new ApiResponse("Alumnos", alumnoService.listarPaginados(pageable)));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar alumnos por nombre o apellido (paginado)")
    public ResponseEntity<ApiResponse> buscar(
            @RequestParam(required = false, defaultValue = "") String nombre,
            @RequestParam(required = false, defaultValue = "") String apellido,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new ApiResponse("Resultados",
                alumnoService.buscarPorNombre(nombre, apellido, pageable)));
    }

    @GetMapping("/habilitados")
    @Operation(summary = "Listar solo alumnos habilitados")
    public ResponseEntity<ApiResponse> habilitados() {
        return ResponseEntity.ok(new ApiResponse("Alumnos habilitados",
                alumnoService.listarHabilitados()));
    }

    @GetMapping("/por-carrera/{carreraId}")
    @Operation(summary = "Listar alumnos de una carrera")
    public ResponseEntity<ApiResponse> porCarrera(@PathVariable Long carreraId) {
        return ResponseEntity.ok(new ApiResponse("Alumnos de la carrera",
                alumnoService.listarPorCarrera(carreraId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener alumno por ID")
    public ResponseEntity<ApiResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Alumno", alumnoService.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un alumno")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long id,
                                                   @RequestBody Alumno datos) {
        return ResponseEntity.ok(new ApiResponse("Alumno actualizado",
                alumnoService.actualizar(id, datos)));
    }

    @PutMapping("/{id}/habilitar")
    @Operation(summary = "Habilitar alumno")
    public ResponseEntity<ApiResponse> habilitar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Alumno habilitado",
                alumnoService.habilitar(id)));
    }

    @PutMapping("/{id}/deshabilitar")
    @Operation(summary = "Deshabilitar alumno")
    public ResponseEntity<ApiResponse> deshabilitar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Alumno deshabilitado",
                alumnoService.deshabilitar(id)));
    }
}
