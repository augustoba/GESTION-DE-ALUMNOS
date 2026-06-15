package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.DiaSemana;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record HorarioClaseRequest(
        @NotNull DiaSemana diaSemana,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin,
        @NotNull LocalDate fechaInicioCursada,
        @NotNull LocalDate fechaFinCursada,
        Long aulaId,
        Long docenteId
) {}
