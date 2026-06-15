package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.OtorgarPermisoRequest;
import coviello.gestion_de_alumnos.service.PermisoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permisos")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Permisos", description = "Gestión de permisos granulares para ADMIN. Solo SUPER_ADMIN.")
public class PermisoController {

    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Ver permisos activos de un usuario (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> listarActivos(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(new ApiResponse("Permisos activos",
                permisoService.listarPermisosActivos(usuarioId)));
    }

    @PostMapping
    @Operation(summary = "Otorgar permiso a un ADMIN (SUPER_ADMIN)",
               description = "Si fechaHasta es null el permiso no expira.")
    public ResponseEntity<ApiResponse> otorgar(@RequestBody OtorgarPermisoRequest req,
                                                Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Permiso otorgado",
                        permisoService.otorgarPermiso(req, auth.getName())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Revocar permiso (SUPER_ADMIN)",
               description = "Fija fechaHasta = ahora, el permiso queda inactivo inmediatamente.")
    public ResponseEntity<ApiResponse> revocar(@PathVariable Long id) {
        permisoService.revocarPermiso(id);
        return ResponseEntity.ok(new ApiResponse("Permiso revocado", null));
    }
}
