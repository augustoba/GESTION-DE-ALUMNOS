package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.ActualizarPerfilRequest;
import coviello.gestion_de_alumnos.dto.DocumentoResumen;
import coviello.gestion_de_alumnos.dto.PerfilResponse;
import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.model.DocumentoDigital;
import coviello.gestion_de_alumnos.model.TipoDocumento;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
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

    public PerfilController(AlumnoRepository alumnoRepository,
                            DocumentoDigitalService documentoDigitalService) {
        this.alumnoRepository = alumnoRepository;
        this.documentoDigitalService = documentoDigitalService;
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
    @Operation(summary = "Subir documento digital (ALUMNO)",
               description = "Sube un archivo (imagen o PDF) para el tipo de documento indicado. Reemplaza el anterior si ya existía.")
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

    private Alumno resolverAlumno(String email) {
        return alumnoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No se encontró un alumno para: " + email));
    }

    private PerfilResponse toResponse(Alumno a) {
        return new PerfilResponse(a.getNombres(), a.getApellidos(), a.getDni(), a.getEmail(),
                a.getTelefono(), a.getDireccion(), a.getFechaNac(), a.isHabilitado());
    }
}
