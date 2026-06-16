package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.*;
import coviello.gestion_de_alumnos.model.EstadoPreinscripcion;
import coviello.gestion_de_alumnos.model.TipoDocumento;
import coviello.gestion_de_alumnos.service.*;
import coviello.gestion_de_alumnos.service.ConfiguracionSistemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/preinscripciones")
@Tag(name = "Preinscripciones", description = "Gestión del proceso presencial de inscripción.")
public class PreinscripcionController {

    private final PreinscripcionService preinscripcionService;
    private final PagoService pagoService;
    private final DocumentoChecklistService checklistService;
    private final TurnoService turnoService;
    private final ConfiguracionSistemaService configuracionService;

    public PreinscripcionController(PreinscripcionService preinscripcionService,
                                    PagoService pagoService,
                                    DocumentoChecklistService checklistService,
                                    TurnoService turnoService,
                                    ConfiguracionSistemaService configuracionService) {
        this.preinscripcionService = preinscripcionService;
        this.pagoService = pagoService;
        this.checklistService = checklistService;
        this.turnoService = turnoService;
        this.configuracionService = configuracionService;
    }

    // ── Crear ─────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Crear formulario de preinscripción",
               description = "Genera el código PRE-YYYY-NNNNN y envía el PDF por email al aspirante.")
    public ResponseEntity<ApiResponse> crear(@Valid @RequestBody PreinscripcionRequest req) {
        if (!configuracionService.isPreinscripcionHabilitada()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("El período de preinscripción está cerrado.", null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Formulario registrado. Se envió el PDF al email del aspirante.",
                        preinscripcionService.crear(req)));
    }

    // ── Consultas ─────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Listar todas las preinscripciones paginadas (ADMIN)")
    public ResponseEntity<ApiResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(new ApiResponse("Preinscripciones",
                preinscripcionService.listarTodas(page, size)));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Listar preinscripciones por estado (ADMIN)")
    public ResponseEntity<ApiResponse> listarPorEstado(
            @PathVariable EstadoPreinscripcion estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(new ApiResponse("Preinscripciones en estado " + estado,
                preinscripcionService.listarPorEstado(estado, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Detalle completo con pago y checklist (ADMIN)")
    public ResponseEntity<ApiResponse> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Detalle de preinscripción",
                preinscripcionService.obtenerDetalle(id)));
    }

    @GetMapping("/buscar/codigo")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Buscar por código o número de formulario (ADMIN) — búsqueda parcial")
    public ResponseEntity<ApiResponse> buscarPorCodigo(@RequestParam String codigo) {
        var resultados = preinscripcionService.buscarPorCodigo(codigo);
        if (resultados.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("No se encontró ninguna preinscripción con ese código.", null));
        }
        return ResponseEntity.ok(new ApiResponse("Preinscripciones encontradas", resultados));
    }

    @GetMapping("/buscar/dni")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Buscar por DNI (ADMIN)")
    public ResponseEntity<ApiResponse> buscarPorDni(@RequestParam String dni) {
        return ResponseEntity.ok(new ApiResponse("Preinscripción encontrada",
                preinscripcionService.buscarPorDni(dni)));
    }

    @GetMapping("/buscar/nombre")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Buscar por nombre o apellido paginado (ADMIN)")
    public ResponseEntity<ApiResponse> buscarPorNombre(
            @RequestParam(required = false, defaultValue = "") String nombre,
            @RequestParam(required = false, defaultValue = "") String apellido,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(new ApiResponse("Resultados de búsqueda",
                preinscripcionService.buscarPorNombre(apellido, nombre, page, size)));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Descargar/ver PDF del formulario (ADMIN)")
    public ResponseEntity<byte[]> verPdf(@PathVariable Long id) {
        byte[] pdf = preinscripcionService.generarPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "formulario-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    // ── Gestión de estado ─────────────────────────────────────────

    @PutMapping("/{id}/en-revision")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Poner en revisión (ADMIN)")
    public ResponseEntity<ApiResponse> enRevision(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Preinscripción puesta en revisión",
                preinscripcionService.cambiarEstado(id, EstadoPreinscripcion.EN_REVISION)));
    }

    @PutMapping("/{id}/habilitar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Habilitar como alumno (ADMIN)",
               description = "Crea el usuario alumno, lo asigna a la comisión indicada y envía email de bienvenida.")
    public ResponseEntity<ApiResponse> habilitar(@PathVariable Long id,
                                                  @RequestBody HabilitarAlumnoRequest req) {
        return ResponseEntity.ok(new ApiResponse("Alumno habilitado. Se envió email de bienvenida.",
                preinscripcionService.habilitarComoAlumno(id, req.comisionId())));
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Rechazar preinscripción (ADMIN)")
    public ResponseEntity<ApiResponse> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Preinscripción rechazada",
                preinscripcionService.rechazar(id)));
    }

    // ── Pago en efectivo ──────────────────────────────────────────

    @PostMapping("/{id}/pago")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Registrar/actualizar pago en efectivo (ADMIN)",
               description = "Acumulativo: suma al monto ya abonado. Estado: SIN_PAGO / PARCIAL / COMPLETO.")
    public ResponseEntity<ApiResponse> registrarPago(@PathVariable Long id,
                                                      @RequestBody PagoRequest req,
                                                      Authentication auth) {
        return ResponseEntity.ok(new ApiResponse("Pago registrado",
                pagoService.registrarPago(id, req, auth.getName())));
    }

    @GetMapping("/{id}/pago")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Ver estado del pago (ADMIN)")
    public ResponseEntity<ApiResponse> verPago(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Pago", pagoService.obtenerPorPreinscripcion(id)));
    }

    @DeleteMapping("/{id}/pago")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Anular pago (ADMIN)")
    public ResponseEntity<ApiResponse> anularPago(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Pago anulado", pagoService.anularPago(id)));
    }

    // ── Checklist de documentos físicos ──────────────────────────

    @GetMapping("/{id}/checklist")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Ver checklist de documentos físicos (ADMIN)")
    public ResponseEntity<ApiResponse> verChecklist(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Checklist de documentos",
                checklistService.listarPorPreinscripcion(id)));
    }

    @PostMapping("/{id}/checklist/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Marcar documento físico como presentado (ADMIN)")
    public ResponseEntity<ApiResponse> marcarPresentado(@PathVariable Long id,
                                                         @PathVariable TipoDocumento tipo,
                                                         Authentication auth) {
        return ResponseEntity.ok(new ApiResponse("Documento marcado como presentado",
                checklistService.marcarPresentado(id, tipo, auth.getName())));
    }

    @DeleteMapping("/{id}/checklist/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Desmarcar documento físico (ADMIN)")
    public ResponseEntity<ApiResponse> desmarcarPresentado(@PathVariable Long id,
                                                            @PathVariable TipoDocumento tipo) {
        return ResponseEntity.ok(new ApiResponse("Documento desmarcado",
                checklistService.desmarcarPresentado(id, tipo)));
    }

    // ── Turno ─────────────────────────────────────────────────────

    @PostMapping("/{id}/turno")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Asignar turno de atención (ADMIN)",
               description = "Genera el número de turno y envía email con link de confirmación.")
    public ResponseEntity<ApiResponse> asignarTurno(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Turno asignado. Se envió email al aspirante.",
                turnoService.asignarTurno(id)));
    }

    @GetMapping("/{id}/turno")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Ver turno asignado (ADMIN)")
    public ResponseEntity<ApiResponse> verTurno(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Turno",
                turnoService.obtenerPorPreinscripcion(id)));
    }
}
