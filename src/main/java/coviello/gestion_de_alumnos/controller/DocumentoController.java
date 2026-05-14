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
@Tag(name = "Documentos", description = "Carga y validación de documentación obligatoria: DNI frente, DNI dorso, título secundario y foto carnet.")
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
            El alumno sube uno de los cuatro documentos requeridos.
            Si ya existe un documento del mismo tipo, se reemplaza y su estado vuelve a PENDIENTE.
            Tipos aceptados: DNI_FRENTE, DNI_DORSO, TITULO, FOTO_CARNET.
            Formatos recomendados: JPG, PNG para imágenes; PDF para el título secundario.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Documento subido correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "La preinscripción no existe o está expirada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos para realizar esta acción.")
    })
    public ResponseEntity<ApiResponse> subirDocumento(
            @Parameter(description = "ID de la preinscripción", example = "1")
            @PathVariable Long preinscripcionId,
            @Parameter(description = "Tipo de documento. Valores: DNI_FRENTE, DNI_DORSO, TITULO, FOTO_CARNET", example = "DNI_FRENTE")
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
    @PreAuthorize("hasAnyRole('ALUMNO', 'ADMIN')")
    @Operation(
        summary = "Listar documentos de una preinscripción",
        description = """
            Devuelve la lista de documentos subidos para una preinscripción, con su estado actual:
            PENDIENTE (en revisión), VALIDADO (aprobado) o RESUBIR (debe volver a subirse).
            El campo 'archivo' (binario) no se incluye en el JSON.
            Para descargar un archivo usá GET /api/documentos/{id}/descargar.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista devuelta correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Preinscripción no encontrada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos para realizar esta acción.")
    })
    public ResponseEntity<ApiResponse> listarPorPreinscripcion(
            @Parameter(description = "ID de la preinscripción", example = "1")
            @PathVariable Long preinscripcionId) {

        List<Documento> documentos = documentoService.listarPorPreinscripcion(preinscripcionId);
        return ResponseEntity.ok(new ApiResponse("Documentos de la preinscripción", documentos));
    }

    @GetMapping("/{documentoId}/descargar")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO')")
    @Operation(
        summary = "Ver/descargar archivo de un documento (ADMIN, ALUMNO)",
        description = "Devuelve el archivo binario del documento. El navegador lo mostrará en línea (PDF/imagen) o lo descargará según el tipo."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Archivo devuelto correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Documento no encontrado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos para realizar esta acción.")
    })
    public ResponseEntity<byte[]> descargarDocumento(
            @Parameter(description = "ID del documento a ver/descargar", example = "1")
            @PathVariable Long documentoId) {

        Documento documento = documentoService.obtenerPorId(documentoId);

        String contentType = documento.getContentType() != null
                ? documento.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        String nombreArchivo = documento.getNombreArchivo() != null
                ? documento.getNombreArchivo()
                : "documento_" + documentoId;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(documento.getArchivo());
    }

    @PutMapping("/{documentoId}/validar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Validar un documento (ADMIN)",
        description = """
            El admin marca el documento como VALIDADO.
            Cuando los cuatro documentos obligatorios (DNI_FRENTE, DNI_DORSO, TITULO y FOTO_CARNET)
            queden todos en estado VALIDADO, la preinscripción pasa automáticamente a DOCUMENTOS_COMPLETOS.
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

    @PutMapping("/{documentoId}/resubir")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Solicitar resubida de un documento (ADMIN)",
        description = """
            El admin marca el documento como RESUBIR porque está incompleto, es ilegible
            o no corresponde al tipo indicado.
            El alumno verá el estado RESUBIR en su lista de documentos y deberá volver a subir ese archivo.
            Al subir el nuevo archivo, el estado vuelve automáticamente a PENDIENTE.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Documento marcado como RESUBIR. El alumno deberá volver a subir el archivo."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Documento no encontrado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> solicitarResubida(
            @Parameter(description = "ID del documento a solicitar resubida", example = "1")
            @PathVariable Long documentoId) {

        Documento documento = documentoService.solicitarResubida(documentoId);
        return ResponseEntity.ok(new ApiResponse("Se solicitó la resubida del documento. El alumno deberá volver a subir el archivo.", documento));
    }
}
