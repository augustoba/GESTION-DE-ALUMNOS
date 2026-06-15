package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.DiaSemana;

import java.time.LocalDate;
import java.time.LocalTime;

public record HorarioClaseResponse(
        Long id,
        DiaSemana diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        LocalDate fechaInicioCursada,
        LocalDate fechaFinCursada,
        String aulaNombre,
        Long aulaId,
        String docenteNombre,
        Long docenteId
) {}
