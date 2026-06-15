package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.service.ConfiguracionSistemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/configuracion")
@Tag(name = "Configuración del sistema", description = "Ajustes globales gestionados por el SUPER_ADMIN.")
public class ConfiguracionSistemaController {

    private final ConfiguracionSistemaService service;

    public ConfiguracionSistemaController(ConfiguracionSistemaService service) {
        this.service = service;
    }

    @GetMapping("/preinscripcion")
    @Operation(summary = "Estado del formulario de preinscripción (público)")
    public ResponseEntity<ApiResponse> estadoPreinscripcion() {
        boolean habilitada = service.isPreinscripcionHabilitada();
        return ResponseEntity.ok(new ApiResponse("Estado de preinscripción",
                Map.of("habilitada", habilitada)));
    }

    @PutMapping("/preinscripcion")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Habilitar o deshabilitar el formulario de preinscripción (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> setPreinscripcion(@RequestBody Map<String, Boolean> body) {
        boolean habilitada = Boolean.TRUE.equals(body.get("habilitada"));
        service.setPreinscripcionHabilitada(habilitada);
        String msg = habilitada ? "Preinscripción habilitada" : "Preinscripción deshabilitada";
        return ResponseEntity.ok(new ApiResponse(msg, Map.of("habilitada", habilitada)));
    }

    @GetMapping("/turnos")
    @Operation(summary = "Estado de la solicitud de turnos (público)")
    public ResponseEntity<ApiResponse> estadoTurnos() {
        boolean habilitados = service.isTurnosHabilitados();
        return ResponseEntity.ok(new ApiResponse("Estado de turnos",
                Map.of("habilitados", habilitados)));
    }

    @PutMapping("/turnos")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Habilitar o deshabilitar solicitud de turnos (SUPER_ADMIN). Al habilitar envía email masivo.")
    public ResponseEntity<ApiResponse> setTurnos(@RequestBody Map<String, Boolean> body) {
        boolean habilitado = Boolean.TRUE.equals(body.get("habilitado"));
        service.setTurnosHabilitados(habilitado);
        String msg = habilitado
                ? "Turnos habilitados. Se enviaron notificaciones a los aspirantes."
                : "Turnos deshabilitados";
        return ResponseEntity.ok(new ApiResponse(msg, Map.of("habilitados", habilitado)));
    }
}
