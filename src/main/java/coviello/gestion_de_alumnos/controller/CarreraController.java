package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.service.CarreraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carreras")
@Tag(name = "Carreras", description = "Gestión de carreras disponibles para preinscripción.")
public class CarreraController {

    private final CarreraService carreraService;

    public CarreraController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN')")
    @Operation(
        summary = "Listar carreras activas",
        description = "Devuelve todas las carreras disponibles para preinscripción."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista devuelta correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado.")
    })
    public ResponseEntity<ApiResponse> listarActivas() {
        List<Carrera> carreras = carreraService.obtenerTodas()
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getActiva()))
                .toList();
        return ResponseEntity.ok(new ApiResponse("Carreras disponibles", carreras));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN')")
    @Operation(summary = "Obtener una carrera por ID")
    public ResponseEntity<ApiResponse> obtenerPorId(
            @Parameter(description = "ID de la carrera", example = "1")
            @PathVariable Long id) {
        Carrera carrera = carreraService.obtenerPorId(id);
        return ResponseEntity.ok(new ApiResponse("Carrera encontrada", carrera));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Crear nueva carrera (ADMIN)",
        description = "Crea una nueva carrera. El campo 'activa' determina si aparece en el listado para los alumnos."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Carrera creada correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN.")
    })
    public ResponseEntity<ApiResponse> crear(@RequestBody Carrera carrera) {
        Carrera nueva = carreraService.crearCarrera(carrera);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Carrera creada correctamente", nueva));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una carrera (ADMIN)")
    public ResponseEntity<ApiResponse> actualizar(
            @PathVariable Long id,
            @RequestBody Carrera carrera) {
        Carrera actualizada = carreraService.actualizarCarrera(id, carrera);
        return ResponseEntity.ok(new ApiResponse("Carrera actualizada correctamente", actualizada));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una carrera (ADMIN)")
    public ResponseEntity<ApiResponse> eliminar(
            @Parameter(description = "ID de la carrera a eliminar", example = "1")
            @PathVariable Long id) {
        carreraService.eliminarCarrera(id);
        return ResponseEntity.ok(new ApiResponse("Carrera eliminada correctamente", null));
    }
}
