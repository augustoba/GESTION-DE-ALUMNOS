package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.TipoPeriodoInscripcion;

import java.time.LocalDate;

public record PeriodoInscripcionResponse(
        Long id,
        String nombre,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        boolean activo,
        TipoPeriodoInscripcion tipo
) {}
