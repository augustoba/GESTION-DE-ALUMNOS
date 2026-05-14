package coviello.gestion_de_alumnos.dto;

public record MateriaRequest(
    String nombre,
    String descripcion,
    String diaSemana,
    String horaInicio,
    String horaFin,
    String aula,
    Long docenteId
) {}
