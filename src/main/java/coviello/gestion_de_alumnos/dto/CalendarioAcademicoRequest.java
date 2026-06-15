package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.AlcanceCalendario;
import coviello.gestion_de_alumnos.model.TipoCalendario;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CalendarioAcademicoRequest(
        @NotNull LocalDate fecha,
        @NotNull TipoCalendario tipo,
        @NotNull String descripcion,
        @NotNull AlcanceCalendario afectaA,
        Long carreraId
) {}
