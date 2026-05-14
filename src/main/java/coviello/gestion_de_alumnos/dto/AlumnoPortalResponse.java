package coviello.gestion_de_alumnos.dto;

public record AlumnoPortalResponse(
        Long id,
        String nombre,
        String apellido,
        String dni,
        String email,
        String telefono,
        String carreraNombre
) {}
