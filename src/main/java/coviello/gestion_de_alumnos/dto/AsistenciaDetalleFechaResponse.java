package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.EstadoAsistencia;
import coviello.gestion_de_alumnos.model.MetodoAsistencia;

import java.time.LocalDate;
import java.time.LocalTime;

public record AsistenciaDetalleFechaResponse(
        Long asistenciaId,
        Long alumnoId,
        String alumnoNombre,
        String alumnoDni,
        LocalDate fecha,
        LocalTime horaRegistro,
        EstadoAsistencia estado,
        MetodoAsistencia metodo,
        String justificacion
) {}
