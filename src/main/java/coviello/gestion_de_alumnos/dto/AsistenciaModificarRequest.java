package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.EstadoAsistencia;
import jakarta.validation.constraints.NotNull;

public record AsistenciaModificarRequest(
        @NotNull EstadoAsistencia estado,
        String justificacion
) {}
