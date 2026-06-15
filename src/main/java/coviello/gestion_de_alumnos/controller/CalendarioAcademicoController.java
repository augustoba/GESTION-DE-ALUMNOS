package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.CalendarioAcademicoRequest;
import coviello.gestion_de_alumnos.model.TipoCalendario;
import coviello.gestion_de_alumnos.service.CalendarioAcademicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/calendario")
@Tag(name = "Calendario Académico", description = "Feriados, eventos y días no cursables.")
public class CalendarioAcademicoController {

    private final CalendarioAcademicoService calendarioService;

    public CalendarioAcademicoController(CalendarioAcademicoService calendarioService) {
        this.calendarioService = calendarioService;
    }

    @GetMapping
    @Operation(summary = "Listar eventos por rango de fechas (público)")
    public ResponseEntity<ApiResponse> listarPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(new ApiResponse("Eventos del calendario",
                calendarioService.listarPorRango(desde, hasta)));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar eventos por tipo (público)")
    public ResponseEntity<ApiResponse> listarPorTipo(@PathVariable TipoCalendario tipo) {
        return ResponseEntity.ok(new ApiResponse("Eventos de tipo " + tipo,
                calendarioService.listarPorTipo(tipo)));
    }

    @GetMapping("/no-cursables/{carreraId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DOCENTE')")
    @Operation(summary = "Días no cursables de una carrera en un rango (ADMIN/DOCENTE)")
    public ResponseEntity<ApiResponse> diasNoCursables(
            @PathVariable Long carreraId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(new ApiResponse("Días no cursables",
                calendarioService.listarDiasNoCursables(carreraId, desde, hasta)));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Agregar evento al calendario (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> crear(@RequestBody CalendarioAcademicoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Evento creado", calendarioService.crear(req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Eliminar evento del calendario (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        calendarioService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse("Evento eliminado", null));
    }
}
