package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.EnvioMailRequest;
import coviello.gestion_de_alumnos.service.EnvioMailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mails")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Envío de Emails", description = "Envío masivo de emails a alumnos con historial.")
public class EnvioMailController {

    private final EnvioMailService envioMailService;

    public EnvioMailController(EnvioMailService envioMailService) {
        this.envioMailService = envioMailService;
    }

    @PostMapping("/enviar")
    @Operation(summary = "Enviar email masivo (ADMIN/SUPER_ADMIN)",
               description = "destinatarioTipo: TODOS | POR_CARRERA | POR_ANIO | DOCS_FALTANTES")
    public ResponseEntity<ApiResponse> enviar(@RequestBody EnvioMailRequest req,
                                               Authentication auth) {
        return ResponseEntity.ok(new ApiResponse("Emails enviados",
                envioMailService.enviar(req, auth.getName())));
    }

    @GetMapping("/historial")
    @Operation(summary = "Ver historial de envíos (ADMIN/SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> historial(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(new ApiResponse("Historial de envíos",
                envioMailService.listarHistorial(page, size)));
    }
}
