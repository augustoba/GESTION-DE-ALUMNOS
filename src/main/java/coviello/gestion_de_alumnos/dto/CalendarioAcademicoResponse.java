package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.AlcanceCalendario;
import coviello.gestion_de_alumnos.model.TipoCalendario;

import java.time.LocalDate;

public record CalendarioAcademicoResponse(
        Long id,
        LocalDate fecha,
        TipoCalendario tipo,
        String descripcion,
        AlcanceCalendario afectaA,
        String carreraNombre
) {}
