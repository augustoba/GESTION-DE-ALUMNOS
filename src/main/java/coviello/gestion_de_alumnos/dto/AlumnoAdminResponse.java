package coviello.gestion_de_alumnos.dto;

public record AlumnoAdminResponse(
    Long id,
    String nombres,
    String apellidos,
    String dni,
    String email,
    String telefono,
    boolean habilitado,
    Long comisionId,
    String comisionNombre,
    Integer anioNumero,
    Long carreraId,
    String carreraNombre
) {}
