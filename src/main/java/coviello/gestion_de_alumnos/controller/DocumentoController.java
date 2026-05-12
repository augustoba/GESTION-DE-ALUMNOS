package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.model.Documento;
import coviello.gestion_de_alumnos.model.TipoDocumento;
import coviello.gestion_de_alumnos.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@Tag(name = "Documentos", description = "Carga y validación de documentación obligatoria: DNI frente, DNI dorso y título secundario.")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @PostMapping(value = "/preinscripcion/{preinscripcionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN')")
    @Operation(
        summary = "Subir documento (ALUMNO)",
        description = """
            El alumno sube uno de los documentos requeridos para completar su inscripción.
            Solo disponible cuando la preinscripción está en estado PAGO_VALIDADO o DOCUMENTOS_COMPLETOS.
            Si ya existe un documento del mismo tipo, se reemplaza y se resetea su validación.
            Tipos aceptados: DNI_FRENTE, DNI_DORSO, TITULO, FOTO.
            Formatos recomendados: JPG, PNG, PDF.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Documento subido correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "La preinscripción no existe, está expirada o el pago aún no fue validado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos para realizar esta acción.")
    })
    public ResponseEntity<ApiResponse> subirDocumento(
            @Parameter(description = "ID de la preinscripción a la que pertenece el documento", example = "1")
            @PathVariable Long preinscripcionId,
            @Parameter(description = "Tipo de documento. Valores: DNI_FRENTE, DNI_DORSO, TITULO, FOTO", example = "DNI_FRENTE")
            @RequestParam TipoDocumento tipo,
            @Parameter(description = "Archivo del documento (JPG, PNG o PDF)")
            @RequestParam("archivo") MultipartFile archivo) {

        try {
            Documento guardado = documentoService.subirDocumento(preinscripcionId, tipo, archivo);
            return ResponseEntity.ok(new ApiResponse("Documento subido correctamente", guardado));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Error al guardar el archivo: " + e.getMessage(), null));
        }
    }

    @GetMapping("/preinscripcion/{preinscripcionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar documentos de una preinscripción (ADMIN)",
        description = """
            Devuelve la lista de documentos subidos para una preinscripción.
            El campo 'archivo' (binario) no se incluye en el JSON para no sobrecargar la respuesta.
            Para descargar un archivo, usá el endpoint GET /api/documentos/{id}/descargar con el ID del documento.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista devuelta correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Preinscripción no encontrada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> listarPorPreinscripcion(
            @Parameter(description = "ID de la preinscripción", example = "1")
            @PathVariable Long preinscripcionId) {

        List<Documento> documentos = documentoService.listarPorPreinscripcion(preinscripcionId);
        return ResponseEntity.ok(new ApiResponse("Documentos de la preinscripción", documentos));
    }

    @GetMapping("/{documentoId}/descargar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Descargar archivo de un documento (ADMIN)",
        description = """
            Descarga el archivo binario del documento (imagen o PDF).
            El cliente recibirá el archivo con su nombre y tipo de contenido original.
            Nota: este endpoint no devuelve JSON, devuelve el archivo directamente.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Archivo descargado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Documento no encontrado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<byte[]> descargarDocumento(
            @Parameter(description = "ID del documento a descargar", example = "1")
            @PathVariable Long documentoId) {

        Documento documento = documentoService.obtenerPorId(documentoId);

        String contentType = documento.getContentType() != null
                ? documento.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        String nombreArchivo = documento.getNombreArchivo() != null
                ? documento.getNombreArchivo()
                : "documento_" + documentoId;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(documento.getArchivo());
    }

    @PutMapping("/{documentoId}/validar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Validar un documento (ADMIN)",
        description = """
            El admin marca el documento como válido.
            Cuando los tres documentos obligatorios (DNI_FRENTE, DNI_DORSO y TITULO) queden todos validados,
            la preinscripción pasa automáticamente a DOCUMENTOS_COMPLETOS, finalizando el proceso de inscripción.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Documento validado. Si era el último obligatorio, la preinscripción queda completa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Documento no encontrado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> validarDocumento(
            @Parameter(description = "ID del documento a validar", example = "1")
            @PathVariable Long documentoId) {

        Documento documento = documentoService.validarDocumento(documentoId);
        return ResponseEntity.ok(new ApiResponse("Documento validado correctamente", documento));
    }

    @PutMapping("/{documentoId}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Rechazar un documento (ADMIN)",
        description = """
            El admin rechaza el documento porque está incompleto, es ilegible o no corresponde al tipo indicado.
            El alumno deberá volver a subir ese documento desde el endpoint de carga.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Documento rechazado. El alumno deberá volver a subirlo."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Documento no encontrado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> rechazarDocumento(
            @Parameter(description = "ID del documento a rechazar", example = "1")
            @PathVariable Long documentoId) {

        Documento documento = documentoService.rechazarDocumento(documentoId);
        return ResponseEntity.ok(new ApiResponse("Documento rechazado. El alumno deberá volver a subirlo.", documento));
    }
}
