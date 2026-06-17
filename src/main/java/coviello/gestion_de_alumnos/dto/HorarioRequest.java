package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.DiaSemana;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record HorarioRequest(
        @NotNull(message = "El día de semana es obligatorio") DiaSemana diaSemana,
        @NotNull(message = "La hora de inicio es obligatoria") LocalTime horaInicio,
        @NotNull(message = "La hora de fin es obligatoria") LocalTime horaFin,
        LocalDate fechaInicioCursada,
        LocalDate fechaFinCursada
) {}
