package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.*;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import coviello.gestion_de_alumnos.service.DocumentoDigitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/perfil")
@Tag(name = "Perfil", description = "Datos del usuario autenticado.")
public class PerfilController {

    @Value("${app.upload.dir:uploads/documentos}")
    private String uploadDir;

    private final AlumnoRepository alumnoRepository;
    private final DocumentoDigitalService documentoDigitalService;
    private final InscripcionMateriaRepository inscripcionMateriaRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final ConfiguracionAsistenciaRepository configAsistenciaRepository;

    public PerfilController(AlumnoRepository alumnoRepository,
                            DocumentoDigitalService documentoDigitalService,
                            InscripcionMateriaRepository inscripcionMateriaRepository,
                            AsistenciaRepository asistenciaRepository,
                            ConfiguracionAsistenciaRepository configAsistenciaRepository) {
        this.alumnoRepository = alumnoRepository;
        this.documentoDigitalService = documentoDigitalService;
        this.inscripcionMateriaRepository = inscripcionMateriaRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.configAsistenciaRepository = configAsistenciaRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Obtener perfil del usuario autenticado")
    public ResponseEntity<ApiResponse> obtenerPerfil(Authentication auth) {
        Alumno alumno = resolverAlumno(auth.getName());
        return ResponseEntity.ok(new ApiResponse("Perfil", toResponse(alumno)));
    }

    @PutMapping
    @PreAuthorize("hasRole('ALUMNO')")
    @Operation(summary = "Actualizar dirección y teléfono del alumno autenticado")
    public ResponseEntity<ApiResponse> actualizarPerfil(@RequestBody ActualizarPerfilRequest req,
                                                         Authentication auth) {
        Alumno alumno = resolverAlumno(auth.getName());
        alumno.setDireccion(req.direccion());
        alumno.setTelefono(req.telefono());
        alumnoRepository.save(alumno);
        return ResponseEntity.ok(new ApiResponse("Perfil actualizado", toResponse(alumno)));
    }

    @GetMapping("/documentos")
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Documentos digitales del alumno autenticado")
    public ResponseEntity<ApiResponse> obtenerDocumentos(Authentication auth) {
        Alumno alumno = resolverAlumno(auth.getName());
        List<DocumentoResumen> documentos = documentoDigitalService.listarPorAlumno(alumno.getId())
                .stream()
                .map(d -> new DocumentoResumen(d.getId(), d.getTipoDocumento(),
                        d.getArchivoUrl(), d.getEstado(), d.getMotivoRechazo()))
                .toList();
        return ResponseEntity.ok(new ApiResponse("Documentos del alumno", documentos));
    }

    @PostMapping(value = "/documentos/{tipo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUMNO')")
    @Operation(summary = "Subir documento digital (ALUMNO)")
    public ResponseEntity<ApiResponse> subirDocumento(
            @PathVariable TipoDocumento tipo,
            @RequestParam("archivo") MultipartFile archivo,
            Authentication auth) {
        Alumno alumno = resolverAlumno(auth.getName());
        DocumentoDigital doc = documentoDigitalService.subirDocumento(alumno.getId(), tipo, archivo);
        return ResponseEntity.ok(new ApiResponse("Documento subido correctamente",
                new DocumentoResumen(doc.getId(), doc.getTipoDocumento(),
                        doc.getArchivoUrl(), doc.getEstado(), doc.getMotivoRechazo())));
    }

    @GetMapping("/documentos/archivo/{filename}")
    @Operation(summary = "Servir archivo de documento digital")
    public ResponseEntity<Resource> servirArchivo(@PathVariable String filename) throws IOException {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return ResponseEntity.badRequest().build();
        }
        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) contentType = "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/horarios")
    @PreAuthorize("hasRole('ALUMNO')")
    @Transactional(readOnly = true)
    @Operation(summary = "Horario semanal del alumno: materias confirmadas con día y hora")
    public ResponseEntity<ApiResponse> horarios(Authentication auth) {
        Alumno alumno = resolverAlumno(auth.getName());

        List<HorarioAlumnoItem> items = inscripcionMateriaRepository
                .findByAlumnoIdAndEstado(alumno.getId(), EstadoInscripcionMateria.CONFIRMADA)
                .stream()
                .flatMap(i -> i.getMateria().getHorarios().stream()
                        .map(h -> new HorarioAlumnoItem(
                                i.getMateria().getId(),
                                i.getMateria().getNombre(),
                                h.getDiaSemana().name(),
                                h.getHoraInicio().toString(),
                                h.getHoraFin().toString(),
                                h.getAula() != null ? h.getAula().getNombre() : null
                        ))
                )
                .toList();

        return ResponseEntity.ok(new ApiResponse("Horario del alumno", items));
    }

    @GetMapping("/asistencias-resumen")
    @PreAuthorize("hasRole('ALUMNO')")
    @Transactional(readOnly = true)
    @Operation(summary = "Resumen de asistencia por materia para el alumno autenticado")
    public ResponseEntity<ApiResponse> asistenciasResumen(Authentication auth) {
        Alumno alumno = resolverAlumno(auth.getName());

        List<AsistenciaResumenResponse> resumen = inscripcionMateriaRepository
                .findByAlumnoIdAndEstado(alumno.getId(), EstadoInscripcionMateria.CONFIRMADA)
                .stream()
                .map(i -> {
                    Long materiaId = i.getMateria().getId();
                    String materiaNombre = i.getMateria().getNombre();

                    long presentes = asistenciaRepository.countByAlumnoIdAndHorarioClaseMateriaIdAndEstado(
                            alumno.getId(), materiaId, EstadoAsistencia.PRESENTE);
                    long tardanzas = asistenciaRepository.countByAlumnoIdAndHorarioClaseMateriaIdAndEstado(
                            alumno.getId(), materiaId, EstadoAsistencia.TARDANZA);
                    long ausentes  = asistenciaRepository.countByAlumnoIdAndHorarioClaseMateriaIdAndEstado(
                            alumno.getId(), materiaId, EstadoAsistencia.AUSENTE);

                    long totalClases = presentes + tardanzas + ausentes;
                    long asistidos   = presentes + tardanzas;
                    double porcentaje = totalClases > 0 ? (double) asistidos / totalClases * 100 : 100.0;

                    double minimo = obtenerPorcentajeMinimo(materiaId);
                    boolean libre = porcentaje < minimo;

                    return new AsistenciaResumenResponse(
                            alumno.getId(),
                            alumno.getNombres() + " " + alumno.getApellidos(),
                            materiaId, materiaNombre,
                            totalClases, presentes, tardanzas, ausentes,
                            porcentaje, libre
                    );
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse("Resumen de asistencias", resumen));
    }

    private Alumno resolverAlumno(String email) {
        return alumnoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No se encontró un alumno para: " + email));
    }

    private PerfilResponse toResponse(Alumno a) {
        String carreraNombre = null;
        String comisionNombre = null;
        Integer anioNumero = null;
        if (a.getComision() != null) {
            comisionNombre = a.getComision().getNombre();
            if (a.getComision().getAnioCarrera() != null) {
                anioNumero = a.getComision().getAnioCarrera().getNumeroAnio();
                if (a.getComision().getAnioCarrera().getCarrera() != null) {
                    carreraNombre = a.getComision().getAnioCarrera().getCarrera().getNombre();
                }
            }
        }
        return new PerfilResponse(
                a.getId(), a.getNombres(), a.getApellidos(), a.getDni(), a.getEmail(),
                a.getTelefono(), a.getDireccion(), a.getFechaNac(), a.isHabilitado(),
                carreraNombre, comisionNombre, anioNumero
        );
    }

    private double obtenerPorcentajeMinimo(Long materiaId) {
        return configAsistenciaRepository
                .findByAplicaAAndMateriaId(NivelConfiguracionAsistencia.MATERIA, materiaId)
                .map(c -> (double) c.getPorcentajeMinimo())
                .orElseGet(() -> configAsistenciaRepository
                        .findFirstByAplicaA(NivelConfiguracionAsistencia.GLOBAL)
                        .map(c -> (double) c.getPorcentajeMinimo())
                        .orElse(75.0));
    }
}
