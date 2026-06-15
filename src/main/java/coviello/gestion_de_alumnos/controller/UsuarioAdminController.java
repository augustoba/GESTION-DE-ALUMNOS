package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.AdminCrearRequest;
import coviello.gestion_de_alumnos.service.UsuarioAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Gestión de Usuarios Admin", description = "El SUPER_ADMIN crea y gestiona cuentas ADMIN y DOCENTE.")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;

    public UsuarioAdminController(UsuarioAdminService usuarioAdminService) {
        this.usuarioAdminService = usuarioAdminService;
    }

    @GetMapping
    @Operation(summary = "Listar administradores (SUPER_ADMIN)")
    public ResponseEntity<ApiResponse> listar() {
        return ResponseEntity.ok(new ApiResponse("Administradores", usuarioAdminService.listarAdmins()));
    }

    @PostMapping
    @Operation(summary = "Crear ADMIN o DOCENTE (SUPER_ADMIN)",
               description = "Genera contraseña aleatoria y la envía por email. rol: ADMIN | DOCENTE")
    public ResponseEntity<ApiResponse> crear(@RequestBody AdminCrearRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Usuario creado. Se envió email con credenciales.",
                        usuarioAdminService.crearAdmin(req)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar ADMIN o DOCENTE (SUPER_ADMIN)",
               description = "No se puede desactivar al propio SUPER_ADMIN.")
    public ResponseEntity<ApiResponse> desactivar(@PathVariable Long id) {
        usuarioAdminService.desactivarAdmin(id);
        return ResponseEntity.ok(new ApiResponse("Usuario desactivado", null));
    }
}
