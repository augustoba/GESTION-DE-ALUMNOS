package coviello.gestion_de_alumnos.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PreinscripcionRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 3, max = 100, message = "El apellido debe tener entre 3 y 100 caracteres")
        String apellido,

        @NotBlank(message = "El DNI es obligatorio")
        @Size(min = 6, max = 20, message = "El DNI debe tener entre 6 y 20 caracteres")
        String dni,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        LocalDate fechaNacimiento,

        @Size(min = 3, max = 100, message = "El lugar de nacimiento debe tener al menos 3 caracteres")
        String lugarNacimiento,

        @Size(min = 3, max = 100, message = "La nacionalidad debe tener al menos 3 caracteres")
        String nacionalidad,

        @Size(min = 5, max = 200, message = "La dirección debe tener al menos 5 caracteres")
        String direccion,

        @Size(min = 3, max = 100, message = "La localidad debe tener al menos 3 caracteres")
        String localidad,

        @Size(min = 7, max = 50, message = "El teléfono debe tener al menos 7 caracteres")
        String telefono,

        @Email(message = "El email no tiene un formato válido")
        String email,

        String fotoUrl,
        Long carreraId
) {}
