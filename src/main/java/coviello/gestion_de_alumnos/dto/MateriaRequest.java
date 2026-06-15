package coviello.gestion_de_alumnos.dto;

public record MateriaRequest(
    String nombre,
    String descripcion,
    Long docenteId
) {}
