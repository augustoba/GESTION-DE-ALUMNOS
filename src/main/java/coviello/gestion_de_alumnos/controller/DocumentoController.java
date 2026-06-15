package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.model.TipoDocumento;
import coviello.gestion_de_alumnos.service.DocumentoDigitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/documentos-digitales")
@Tag(name = "Documentos Digitales", description = "Carga y revisión de documentos digitales de alumnos habilitados.")
public class DocumentoController {

    private final DocumentoDigitalService documentoDigitalService;

    public DocumentoController(DocumentoDigitalService documentoDigitalService) {
        this.documentoDigitalService = documentoDigitalService;
    }

    @GetMapping("/alumno/{alumnoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Listar documentos de un alumno (ADMIN)")
    public ResponseEntity<ApiResponse> listarPorAlumno(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(new ApiResponse("Documentos del alumno",
                documentoDigitalService.listarPorAlumno(alumnoId)));
    }

    @PostMapping("/alumno/{alumnoId}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Registrar URL de documento digital (ALUMNO)",
               description = "El alumno registra la URL de su documento subido previamente a almacenamiento externo.")
    public ResponseEntity<ApiResponse> registrar(
            @PathVariable Long alumnoId,
            @RequestParam TipoDocumento tipoDocumento,
            @RequestParam String archivoUrl) {
        return ResponseEntity.ok(new ApiResponse("Documento registrado",
                documentoDigitalService.registrarDocumento(alumnoId, tipoDocumento, archivoUrl)));
    }

    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Aprobar documento (ADMIN)")
    public ResponseEntity<ApiResponse> aprobar(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(new ApiResponse("Documento aprobado",
                documentoDigitalService.aprobar(id, auth.getName())));
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Rechazar documento con motivo (ADMIN)")
    public ResponseEntity<ApiResponse> rechazar(@PathVariable Long id,
                                                 @RequestBody Map<String, String> body,
                                                 Authentication auth) {
        String motivo = body.getOrDefault("motivo", "Documento inválido");
        return ResponseEntity.ok(new ApiResponse("Documento rechazado",
                documentoDigitalService.rechazar(id, motivo, auth.getName())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ALUMNO')")
    @Operation(summary = "Ver un documento por ID")
    public ResponseEntity<ApiResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Documento",
                documentoDigitalService.obtenerPorId(id)));
    }
}
