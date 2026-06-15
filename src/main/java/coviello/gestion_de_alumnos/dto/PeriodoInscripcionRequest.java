package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.TipoPeriodoInscripcion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PeriodoInscripcionRequest(
        @NotBlank String nombre,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin,
        @NotNull TipoPeriodoInscripcion tipo
) {}
