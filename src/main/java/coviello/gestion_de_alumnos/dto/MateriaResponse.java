package coviello.gestion_de_alumnos.dto;

public record MateriaResponse(
    Long id,
    String nombre,
    String descripcion,
    String diaSemana,
    String horaInicio,
    String horaFin,
    String aula,
    DocenteResumen docente
) {}
