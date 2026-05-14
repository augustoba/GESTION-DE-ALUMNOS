package coviello.gestion_de_alumnos.dto;

public record DocenteRequest(
    String nombres,
    String apellidos,
    String dni,
    String email,
    String telefono
) {}
