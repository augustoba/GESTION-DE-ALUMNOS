package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.AlumnoPortalResponse;
import coviello.gestion_de_alumnos.dto.MateriaDetalleDocente;
import coviello.gestion_de_alumnos.service.DocentePortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docente-portal")
@PreAuthorize("hasRole('DOCENTE')")
@Tag(name = "Portal Docente", description = "Endpoints para el portal del docente.")
public class DocentePortalController {

    private final DocentePortalService docentePortalService;

    public DocentePortalController(DocentePortalService docentePortalService) {
        this.docentePortalService = docentePortalService;
    }

    @GetMapping("/mis-materias")
    @Operation(summary = "Materias asignadas al docente autenticado")
    public ResponseEntity<ApiResponse> getMisMaterias(Authentication auth) {
        List<MateriaDetalleDocente> materias = docentePortalService.getMisMaterias(auth.getName());
        return ResponseEntity.ok(new ApiResponse("Materias del docente", materias));
    }

    @GetMapping("/mis-alumnos")
    @Operation(summary = "Alumnos de las carreras del docente autenticado con inscripción aprobada")
    public ResponseEntity<ApiResponse> getMisAlumnos(Authentication auth) {
        List<AlumnoPortalResponse> alumnos = docentePortalService.getMisAlumnos(auth.getName());
        return ResponseEntity.ok(new ApiResponse("Alumnos del docente", alumnos));
    }

    @GetMapping("/alumnos/{id}")
    @Operation(summary = "Detalle de un alumno")
    public ResponseEntity<ApiResponse> getAlumno(@PathVariable Long id) {
        AlumnoPortalResponse alumno = docentePortalService.getAlumno(id);
        return ResponseEntity.ok(new ApiResponse("Alumno", alumno));
    }
}
