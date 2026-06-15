package coviello.gestion_de_alumnos.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurnoResponse(
        Long id,
        String numeroTurno,
        LocalTime horaAsignada,
        LocalDate fechaTurno,
        boolean confirmado,
        String carreraNombre
) {}
