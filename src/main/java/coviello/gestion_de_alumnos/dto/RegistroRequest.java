package coviello.gestion_de_alumnos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(

    @NotBlank(message = "El nombre no puede estar vacío")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$", message = "El nombre no puede contener números ni caracteres especiales")
    String nombres,

    @NotBlank(message = "El apellido no puede estar vacío")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$", message = "El apellido no puede contener números ni caracteres especiales")
    String apellidos,

    @NotBlank(message = "El DNI no puede estar vacío")
    @Pattern(regexp = "^\\d{8,}$", message = "El DNI debe contener al menos 8 dígitos numéricos")
    String dni,

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    String email,

    @NotBlank(message = "La contraseña no puede estar vacía")
    String password

) {}
