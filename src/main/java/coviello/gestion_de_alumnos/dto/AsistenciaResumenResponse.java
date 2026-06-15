package coviello.gestion_de_alumnos.dto;

public record AsistenciaResumenResponse(
        Long alumnoId,
        String alumnoNombre,
        Long materiaId,
        String materiaNombre,
        long totalClases,
        long presentes,
        long tardanzas,
        long ausentes,
        double porcentajeAsistencia,
        boolean libre
) {}
