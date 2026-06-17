package coviello.gestion_de_alumnos.dto;

import java.util.List;

public record MateriaResponse(
        Long id,
        String nombre,
        String descripcion,
        Long anioCarreraId,
        int numeroAnio,
        String carreraNombre,
        DocenteResumen docente,
        List<HorarioResponse> horarios
) {}
