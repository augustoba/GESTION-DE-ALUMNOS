package coviello.gestion_de_alumnos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActivarCuentaRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password
) {}
