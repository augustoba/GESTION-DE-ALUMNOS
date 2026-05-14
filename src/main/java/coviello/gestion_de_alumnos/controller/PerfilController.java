package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.ActualizarPerfilRequest;
import coviello.gestion_de_alumnos.dto.DocumentoResumen;
import coviello.gestion_de_alumnos.dto.PerfilResponse;
import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.model.Preinscripcion;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
import coviello.gestion_de_alumnos.repository.PreinscripcionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/perfil")
@Tag(name = "Perfil", description = "Datos del alumno autenticado.")
public class PerfilController {

    private final AlumnoRepository alumnoRepository;
    private final PreinscripcionRepository preinscripcionRepository;

    public PerfilController(AlumnoRepository alumnoRepository,
                            PreinscripcionRepository preinscripcionRepository) {
        this.alumnoRepository = alumnoRepository;
        this.preinscripcionRepository = preinscripcionRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN')")
    @Operation(summary = "Obtener perfil del alumno autenticado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil devuelto correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado.")
    })
    public ResponseEntity<ApiResponse> obtenerPerfil(Authentication authentication) {
        Alumno alumno = resolverAlumno(authentication.getName());
        return ResponseEntity.ok(new ApiResponse("Perfil del alumno", toResponse(alumno)));
    }

    @PutMapping
    @PreAuthorize("hasRole('ALUMNO')")
    @Operation(summary = "Actualizar dirección y teléfono del alumno autenticado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil actualizado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado.")
    })
    public ResponseEntity<ApiResponse> actualizarPerfil(@RequestBody ActualizarPerfilRequest request,
                                                         Authentication authentication) {
        Alumno alumno = resolverAlumno(authentication.getName());
        alumno.setDireccion(request.direccion());
        alumno.setTelefono(request.telefono());
        alumnoRepository.save(alumno);
        return ResponseEntity.ok(new ApiResponse("Perfil actualizado correctamente", toResponse(alumno)));
    }

    @GetMapping("/documentos")
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN')")
    @Operation(
        summary = "Documentos del alumno autenticado",
        description = "Devuelve la lista de documentos de la preinscripción más reciente del alumno."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Documentos devueltos correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado.")
    })
    public ResponseEntity<ApiResponse> obtenerDocumentos(Authentication authentication) {
        String email = authentication.getName();

        List<Preinscripcion> preinscripciones = preinscripcionRepository.findByEmail(email);

        if (preinscripciones.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse("Sin preinscripción registrada", List.of()));
        }

        Preinscripcion ultima = preinscripciones.stream()
                .max(Comparator.comparing(Preinscripcion::getFechaCreacion))
                .orElseThrow();

        Long preinscripcionId = ultima.getId();
        List<DocumentoResumen> documentos = ultima.getDocumentos() == null
                ? List.of()
                : ultima.getDocumentos().stream()
                        .map(d -> new DocumentoResumen(
                                d.getId(),
                                preinscripcionId,
                                d.getTipo(),
                                d.getNombreArchivo(),
                                d.getContentType(),
                                d.getEstado()))
                        .toList();

        return ResponseEntity.ok(new ApiResponse("Documentos del alumno", documentos));
    }

    private Alumno resolverAlumno(String email) {
        return alumnoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No se encontró un alumno para: " + email));
    }

    private PerfilResponse toResponse(Alumno a) {
        return new PerfilResponse(
                a.getNombres(),
                a.getApellidos(),
                a.getDni(),
                a.getEmail(),
                a.getTelefono(),
                a.getDireccion(),
                a.getFechaNac(),
                a.getStatus()
        );
    }
}
