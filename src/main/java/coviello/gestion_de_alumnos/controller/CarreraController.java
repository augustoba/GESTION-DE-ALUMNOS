package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.*;
import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.service.CarreraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carreras")
@Tag(name = "Carreras", description = "Gestión de carreras, años y materias.")
public class CarreraController {

    private final CarreraService carreraService;

    public CarreraController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    // ── Carreras ─────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN')")
    @Operation(summary = "Listar carreras activas")
    public ResponseEntity<ApiResponse> listarActivas() {
        List<Carrera> carreras = carreraService.obtenerTodas()
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getActiva()))
                .toList();
        return ResponseEntity.ok(new ApiResponse("Carreras disponibles", carreras));
    }

    @GetMapping("/todas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las carreras (ADMIN)")
    public ResponseEntity<ApiResponse> listarTodas() {
        return ResponseEntity.ok(new ApiResponse("Carreras", carreraService.obtenerTodas()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN')")
    @Operation(summary = "Obtener una carrera por ID")
    public ResponseEntity<ApiResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Carrera encontrada", carreraService.obtenerPorId(id)));
    }

    @GetMapping("/{id}/detalle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Detalle completo con años y materias (ADMIN)")
    public ResponseEntity<ApiResponse> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Detalle de carrera", carreraService.obtenerDetalle(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva carrera (ADMIN)")
    public ResponseEntity<ApiResponse> crear(@RequestBody CarreraRequest req) {
        Carrera nueva = carreraService.crearCarrera(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Carrera creada correctamente", nueva));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una carrera (ADMIN)")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long id, @RequestBody CarreraRequest req) {
        return ResponseEntity.ok(new ApiResponse("Carrera actualizada", carreraService.actualizarCarrera(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una carrera (ADMIN)")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        carreraService.eliminarCarrera(id);
        return ResponseEntity.ok(new ApiResponse("Carrera eliminada correctamente", null));
    }

    // ── Años ─────────────────────────────────────────────────────

    @PostMapping("/{carreraId}/anios")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar un año a la carrera (ADMIN)")
    public ResponseEntity<ApiResponse> agregarAnio(
            @PathVariable Long carreraId,
            @RequestBody AnioCarreraRequest req) {
        AnioCarreraResponse anio = carreraService.agregarAnio(carreraId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Año agregado", anio));
    }

    @DeleteMapping("/anios/{anioId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar un año (y sus materias) (ADMIN)")
    public ResponseEntity<ApiResponse> eliminarAnio(@PathVariable Long anioId) {
        carreraService.eliminarAnio(anioId);
        return ResponseEntity.ok(new ApiResponse("Año eliminado correctamente", null));
    }

    // ── Materias ──────────────────────────────────────────────────

    @PostMapping("/anios/{anioId}/materias")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar una materia a un año (ADMIN)")
    public ResponseEntity<ApiResponse> agregarMateria(
            @PathVariable Long anioId,
            @RequestBody MateriaRequest req) {
        MateriaResponse materia = carreraService.agregarMateria(anioId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Materia agregada", materia));
    }

    @PutMapping("/materias/{materiaId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una materia (ADMIN)")
    public ResponseEntity<ApiResponse> actualizarMateria(
            @PathVariable Long materiaId,
            @RequestBody MateriaRequest req) {
        return ResponseEntity.ok(new ApiResponse("Materia actualizada", carreraService.actualizarMateria(materiaId, req)));
    }

    @DeleteMapping("/materias/{materiaId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una materia (ADMIN)")
    public ResponseEntity<ApiResponse> eliminarMateria(@PathVariable Long materiaId) {
        carreraService.eliminarMateria(materiaId);
        return ResponseEntity.ok(new ApiResponse("Materia eliminada correctamente", null));
    }

    // ── Docentes (para selector en frontend) ──────────────────────

    @GetMapping("/docentes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar docentes disponibles (ADMIN)")
    public ResponseEntity<ApiResponse> listarDocentes() {
        return ResponseEntity.ok(new ApiResponse("Docentes", carreraService.listarDocentes()));
    }
}
