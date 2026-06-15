package coviello.gestion_de_alumnos.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DiaInscripcionResponse(
        Long id,
        String nombre,
        LocalDate fecha,
        LocalTime horarioInicio,
        LocalTime horarioFin,
        int cupoMaximo,
        long cupoOcupado,
        long cupoDisponible
) {}
