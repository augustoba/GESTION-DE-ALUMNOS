package coviello.gestion_de_alumnos.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConfiguracionTurnoRequest(
        @NotNull String nombre,
        @NotNull LocalDate fecha,
        @NotNull LocalTime horarioInicio,
        @NotNull LocalTime horarioFin,
        int intervaloMinutos,
        int cupoMaximo
) {}
