package coviello.gestion_de_alumnos.dto;

public record MateriaDetalleDocente(
    Long id,
    String nombre,
    String descripcion,
    String carreraNombre,
    int numeroAnio
) {}
