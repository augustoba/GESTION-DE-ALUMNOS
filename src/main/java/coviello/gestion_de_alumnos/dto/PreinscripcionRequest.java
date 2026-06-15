package coviello.gestion_de_alumnos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PreinscripcionRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank String dni,
        @NotNull  LocalDate fechaNacimiento,
        String lugarNacimiento,
        String nacionalidad,
        String direccion,
        String localidad,
        String telefono,
        @Email String email,
        String fotoUrl,
        Long carreraId
) {}
