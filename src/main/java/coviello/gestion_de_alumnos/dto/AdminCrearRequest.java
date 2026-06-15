package coviello.gestion_de_alumnos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminCrearRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank @Email String email,
        @NotBlank String rol
) {}
