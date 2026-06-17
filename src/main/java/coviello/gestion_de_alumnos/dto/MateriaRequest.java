package coviello.gestion_de_alumnos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MateriaRequest(
        @NotBlank(message = "El nombre de la materia es obligatorio") String nombre,
        String descripcion,
        @NotNull(message = "El año de carrera es obligatorio") Long anioCarreraId,
        Long docenteId,
        @Valid List<HorarioRequest> horarios
) {}
