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
    @Operation(summary = "Listar carreras activas (público)")
    public ResponseEntity<ApiResponse> listarActivas() {
        List<Carrera> carreras = carreraService.obtenerTodas()
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getActiva()))
                .toList();
        return ResponseEntity.ok(new ApiResponse("Carreras disponibles", carreras));
    }

    @GetMapping("/todas")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Listar todas las carreras")
    public ResponseEntity<ApiResponse> listarTodas() {
        return ResponseEntity.ok(new ApiResponse("Carreras", carreraService.obtenerTodas()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Obtener una carrera por ID")
    public ResponseEntity<ApiResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Carrera encontrada", carreraService.obtenerPorId(id)));
    }

    @GetMapping("/{id}/detalle")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Detalle completo con años y materias")
    public ResponseEntity<ApiResponse> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Detalle de carrera", carreraService.obtenerDetalle(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Crear nueva carrera (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> crear(@RequestBody CarreraRequest req) {
        Carrera nueva = carreraService.crearCarrera(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Carrera creada correctamente", nueva));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Actualizar una carrera (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long id, @RequestBody CarreraRequest req) {
        return ResponseEntity.ok(new ApiResponse("Carrera actualizada", carreraService.actualizarCarrera(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Eliminar una carrera (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        carreraService.eliminarCarrera(id);
        return ResponseEntity.ok(new ApiResponse("Carrera eliminada correctamente", null));
    }

    // ── Años ─────────────────────────────────────────────────────

    @PostMapping("/{carreraId}/anios")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Agregar un año a la carrera (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> agregarAnio(
            @PathVariable Long carreraId,
            @RequestBody AnioCarreraRequest req) {
        AnioCarreraResponse anio = carreraService.agregarAnio(carreraId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Año agregado", anio));
    }

    @DeleteMapping("/anios/{anioId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Eliminar un año (y sus materias) (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> eliminarAnio(@PathVariable Long anioId) {
        carreraService.eliminarAnio(anioId);
        return ResponseEntity.ok(new ApiResponse("Año eliminado correctamente", null));
    }

    // ── Materias ──────────────────────────────────────────────────

    @PostMapping("/anios/{anioId}/materias")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Agregar una materia a un año (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> agregarMateria(
            @PathVariable Long anioId,
            @RequestBody MateriaRequest req) {
        MateriaResponse materia = carreraService.agregarMateria(anioId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Materia agregada", materia));
    }

    @PutMapping("/materias/{materiaId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Actualizar una materia (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> actualizarMateria(
            @PathVariable Long materiaId,
            @RequestBody MateriaRequest req) {
        return ResponseEntity.ok(new ApiResponse("Materia actualizada", carreraService.actualizarMateria(materiaId, req)));
    }

    @DeleteMapping("/materias/{materiaId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Eliminar una materia (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> eliminarMateria(@PathVariable Long materiaId) {
        carreraService.eliminarMateria(materiaId);
        return ResponseEntity.ok(new ApiResponse("Materia eliminada correctamente", null));
    }

    // ── Comisiones ────────────────────────────────────────────────

    @PostMapping("/anios/{anioId}/comisiones")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Agregar una comisión a un año (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> agregarComision(
            @PathVariable Long anioId,
            @RequestBody ComisionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Comisión agregada", carreraService.agregarComision(anioId, req)));
    }

    @PutMapping("/comisiones/{comisionId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Actualizar una comisión (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> actualizarComision(
            @PathVariable Long comisionId,
            @RequestBody ComisionRequest req) {
        return ResponseEntity.ok(new ApiResponse("Comisión actualizada",
                carreraService.actualizarComision(comisionId, req)));
    }

    @DeleteMapping("/comisiones/{comisionId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Eliminar una comisión (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> eliminarComision(@PathVariable Long comisionId) {
        carreraService.eliminarComision(comisionId);
        return ResponseEntity.ok(new ApiResponse("Comisión eliminada correctamente", null));
    }

    // ── Docentes (para selector en frontend) ──────────────────────

    @GetMapping("/docentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Listar docentes disponibles")
    public ResponseEntity<ApiResponse> listarDocentes() {
        return ResponseEntity.ok(new ApiResponse("Docentes", carreraService.listarDocentes()));
    }
}
