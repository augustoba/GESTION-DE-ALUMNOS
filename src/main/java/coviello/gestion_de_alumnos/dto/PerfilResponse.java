package coviello.gestion_de_alumnos.dto;

import java.time.LocalDate;

public record PerfilResponse(
        Long alumnoId,
        String nombres,
        String apellidos,
        String dni,
        String email,
        String telefono,
        String direccion,
        LocalDate fechaNac,
        Boolean status,
        String carreraNombre,
        String comisionNombre,
        Integer anioNumero
) {}
