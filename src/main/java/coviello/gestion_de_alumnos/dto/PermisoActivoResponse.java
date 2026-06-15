package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.CodigoPermiso;

import java.time.LocalDateTime;

public record PermisoActivoResponse(
        Long id,
        CodigoPermiso codigo,
        String descripcion,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta,
        String otorgadoPorEmail
) {}
