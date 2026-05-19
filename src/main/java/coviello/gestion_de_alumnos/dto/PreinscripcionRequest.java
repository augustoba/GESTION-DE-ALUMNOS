package coviello.gestion_de_alumnos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PreinscripcionRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank String dni,
        @NotNull  LocalDate fechaNacimiento,
        String lugarNacimiento,
        String nacionalidad,
        String domicilio,
        String localidad,
        String telefono,
        @NotBlank @Email String email,
        String egresadoDe,
        String tituloDe,
        Boolean debeMaterias,
        String materiasAdeudadas,
        String afeccionEspecifica,
        String grupoSanguineo,
        Long carreraId
) {}
