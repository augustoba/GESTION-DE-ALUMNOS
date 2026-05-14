package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.LoginRequest;
import coviello.gestion_de_alumnos.dto.LoginResponse;
import coviello.gestion_de_alumnos.dto.RegistroRequest;
import coviello.gestion_de_alumnos.service.AuthService;
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
