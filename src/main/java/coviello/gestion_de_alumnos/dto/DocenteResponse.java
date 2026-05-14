package coviello.gestion_de_alumnos.dto;

public record DocenteResponse(
    Long id,
    String nombres,
    String apellidos,
    String dni,
    String email,
    String telefono,
    boolean activo
) {}
