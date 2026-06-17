package coviello.gestion_de_alumnos.dto;

import java.util.List;

public record DocenteRequest(
        String nombres,
        String apellidos,
        String dni,
        String email,
        String telefono,
        List<Long> materiasIds
) {}
