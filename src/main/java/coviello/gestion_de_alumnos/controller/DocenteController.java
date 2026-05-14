package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.DocenteRequest;
import coviello.gestion_de_alumnos.dto.DocenteResponse;
import coviello.gestion_de_alumnos.dto.MateriaDetalleDocente;
import coviello.gestion_de_alumnos.service.DocenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docentes")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Docentes", description = "Gestión de docentes (solo ADMIN).")
public class DocenteController {

    private final DocenteService docenteService;

    public DocenteController(DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los docentes")
    public ResponseEntity<ApiResponse> listar() {
        List<DocenteResponse> docentes = docenteService.listarTodos();
        return ResponseEntity.ok(new ApiResponse("Docentes", docentes));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un docente por ID")
    public ResponseEntity<ApiResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Docente encontrado", docenteService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo docente",
               description = "Crea el docente y su usuario. La contraseña inicial es el DNI.")
    public ResponseEntity<ApiResponse> crear(@RequestBody DocenteRequest req) {
        DocenteResponse docente = docenteService.crear(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Docente creado. Contraseña inicial: DNI (" + req.dni() + ")", docente));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un docente")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long id, @RequestBody DocenteRequest req) {
        return ResponseEntity.ok(new ApiResponse("Docente actualizado", docenteService.actualizar(id, req)));
    }

    @GetMapping("/{id}/materias")
    @Operation(summary = "Materias asignadas a un docente con carrera y horario")
    public ResponseEntity<ApiResponse> obtenerMaterias(@PathVariable Long id) {
        List<MateriaDetalleDocente> materias = docenteService.obtenerMaterias(id);
        return ResponseEntity.ok(new ApiResponse("Materias del docente", materias));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Dar de alta o baja a un docente")
    public ResponseEntity<ApiResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        DocenteResponse docente = docenteService.cambiarEstado(id, activo);
        String msg = activo ? "Docente dado de alta" : "Docente dado de baja";
        return ResponseEntity.ok(new ApiResponse(msg, docente));
    }
}
