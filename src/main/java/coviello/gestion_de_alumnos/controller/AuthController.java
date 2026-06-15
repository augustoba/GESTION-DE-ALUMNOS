package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.ActivarCuentaRequest;
import coviello.gestion_de_alumnos.dto.CambiarPasswordRequest;
import coviello.gestion_de_alumnos.dto.LoginRequest;
import coviello.gestion_de_alumnos.dto.LoginResponse;
import coviello.gestion_de_alumnos.dto.RecuperarPasswordRequest;
import coviello.gestion_de_alumnos.dto.RegistroRequest;
import coviello.gestion_de_alumnos.dto.ValidarTokenResponse;
import coviello.gestion_de_alumnos.service.AuthService;
import org.springframework.security.core.Authentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión. Estos endpoints son públicos y no requieren token.")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    @Operation(
        summary = "Registrar nuevo alumno",
        description = """
            Crea una cuenta para un alumno que ingresa por primera vez a la institución.
            Se genera una contraseña automáticamente y se envía al email indicado.
            El alumno debe usar esa contraseña para iniciar sesión en /auth/login.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registro exitoso. Contraseña enviada por email."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "El DNI o el email ya están registrados.")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "nombres": "Juan",
                  "apellidos": "Pérez",
                  "dni": "12345678",
                  "email": "juan.perez@gmail.com",
                  "password": "MiContraseña123"
                }
                """)
        )
    )
    public ResponseEntity<ApiResponse> registro(@Valid @RequestBody RegistroRequest request) {
        try {
            authService.registrar(request);
            return ResponseEntity.ok(new ApiResponse(
                    "Registro exitoso. Te enviamos la contraseña a " + request.email(), null
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/recuperar-password")
    @Operation(
        summary = "Recuperar contraseña",
        description = "Genera una nueva contraseña aleatoria y la envía al email del usuario registrado."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nueva contraseña enviada al email."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "No existe una cuenta con ese email.")
    })
    public ResponseEntity<ApiResponse> recuperarPassword(@Valid @RequestBody RecuperarPasswordRequest request) {
        try {
            authService.recuperarPassword(request.email());
            return ResponseEntity.ok(new ApiResponse(
                    "Se envió una nueva contraseña a " + request.email(), null
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/validar-token")
    @Operation(
        summary = "Validar token de activación",
        description = "Verifica que el token de activación sea válido y no haya expirado. Devuelve el email del usuario."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token válido."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Token inválido o expirado.")
    })
    public ResponseEntity<ApiResponse> validarToken(@RequestParam String token) {
        try {
            ValidarTokenResponse response = authService.validarToken(token);
            return ResponseEntity.ok(new ApiResponse("Token válido", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/activar")
    @Operation(
        summary = "Activar cuenta con token",
        description = "Establece la contraseña definitiva del alumno usando el token recibido por email al ser habilitado."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cuenta activada. Ya puede iniciar sesión."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Token inválido, expirado o contraseña inválida.")
    })
    public ResponseEntity<ApiResponse> activarCuenta(@Valid @RequestBody ActivarCuentaRequest request) {
        try {
            authService.activarCuenta(request);
            return ResponseEntity.ok(new ApiResponse(
                    "¡Cuenta activada! Ya podés iniciar sesión con tu email y nueva contraseña.", null
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/cambiar-password")
    @Operation(summary = "Cambiar contraseña (primer login obligatorio)")
    public ResponseEntity<ApiResponse> cambiarPassword(@RequestBody CambiarPasswordRequest request,
                                                       Authentication auth) {
        try {
            authService.cambiarPassword(auth.getName(), request.passwordActual(), request.passwordNueva());
            return ResponseEntity.ok(new ApiResponse("Contraseña actualizada correctamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token JWT activo")
    public ResponseEntity<ApiResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            LoginResponse response = authService.refreshToken(authHeader);
            return ResponseEntity.ok(new ApiResponse("Token renovado", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = """
            Autentica al usuario y devuelve un token JWT válido por 15 minutos.
            Copiá el valor del campo "data.token" y pegalo en el botón "Authorize" (arriba a la derecha)
            con el formato: Bearer <token>
            Luego todos los endpoints protegidos funcionarán automáticamente.
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso. Devuelve el token JWT."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Credenciales inválidas.")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "username": "juan.perez@gmail.com",
                  "password": "tu-contraseña"
                }
                """)
        )
    )
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse("Login exitoso", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Credenciales inválidas", null));
        }
    }
}
