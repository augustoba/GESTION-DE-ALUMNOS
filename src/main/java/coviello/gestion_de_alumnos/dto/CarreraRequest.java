package coviello.gestion_de_alumnos.dto;

public record CarreraRequest(
    String nombre,
    String descripcion,
    Boolean activa,
    int cupoMaximo
) {}
