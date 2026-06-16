package coviello.gestion_de_alumnos.dto;

public record HorarioAlumnoItem(
        Long materiaId,
        String materiaNombre,
        String diaSemana,
        String horaInicio,
        String horaFin,
        String aula
) {}
