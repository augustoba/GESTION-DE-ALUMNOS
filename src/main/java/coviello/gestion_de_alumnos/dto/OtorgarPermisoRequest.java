package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.CodigoPermiso;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record OtorgarPermisoRequest(
        @NotNull Long usuarioId,
        @NotNull CodigoPermiso codigoPermiso,
        LocalDateTime fechaHasta
) {}
